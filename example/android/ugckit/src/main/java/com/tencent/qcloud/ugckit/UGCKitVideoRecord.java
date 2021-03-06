package com.tencent.qcloud.ugckit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.tencent.liteav.demo.beauty.Beauty;
import com.tencent.liteav.demo.beauty.BeautyParams;
import com.tencent.liteav.demo.beauty.model.ItemInfo;
import com.tencent.liteav.demo.beauty.model.TabInfo;
import com.tencent.liteav.demo.beauty.view.BeautyPanel;
import com.tencent.qcloud.ugckit.basic.ITitleBarLayout;
import com.tencent.qcloud.ugckit.basic.OnUpdateUIListener;
import com.tencent.qcloud.ugckit.basic.UGCKitResult;
import com.tencent.qcloud.ugckit.component.dialog.ProgressDialogUtil;
import com.tencent.qcloud.ugckit.component.dialogfragment.ProgressFragmentUtil;
import com.tencent.qcloud.ugckit.module.ProcessKit;
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK;
import com.tencent.qcloud.ugckit.module.effect.bgm.view.SoundEffectsPannel;
import com.tencent.qcloud.ugckit.module.record.AbsVideoRecordUI;
import com.tencent.qcloud.ugckit.module.record.AudioFocusManager;
import com.tencent.qcloud.ugckit.module.record.MusicInfo;
import com.tencent.qcloud.ugckit.module.record.PhotoSoundPlayer;
import com.tencent.qcloud.ugckit.module.record.RecordBottomLayout;
import com.tencent.qcloud.ugckit.module.record.RecordModeView;
import com.tencent.qcloud.ugckit.module.record.RecordMusicManager;
import com.tencent.qcloud.ugckit.module.record.ScrollFilterView;
import com.tencent.qcloud.ugckit.module.record.UGCKitRecordConfig;
import com.tencent.qcloud.ugckit.module.record.VideoRecordSDK;
import com.tencent.qcloud.ugckit.module.record.interfaces.IRecordButton;
import com.tencent.qcloud.ugckit.module.record.interfaces.IRecordMusicPannel;
import com.tencent.qcloud.ugckit.module.record.interfaces.IRecordRightLayout;
import com.tencent.qcloud.ugckit.utils.DialogUtil;
import com.tencent.qcloud.ugckit.utils.LogReport;
import com.tencent.qcloud.ugckit.utils.TelephonyUtil;
import com.tencent.ugc.TXRecordCommon;
import com.tencent.ugc.TXUGCRecord;
import com.tencent.ugc.TXVideoEditConstants;
import com.tencent.ugc.TXVideoInfoReader;

public class UGCKitVideoRecord extends AbsVideoRecordUI implements
        IRecordRightLayout.OnItemClickListener,
        IRecordButton.OnRecordButtonListener,
        SoundEffectsPannel.SoundEffectsSettingPannelListener,
        IRecordMusicPannel.MusicChangeListener,
        ScrollFilterView.OnRecordFilterListener,
        VideoRecordSDK.OnVideoRecordListener {
    private static final String TAG = "UGCKitVideoRecord";
    
    private OnRecordListener      mOnRecordListener;
    private OnMusicChooseListener mOnMusicListener;
    private FragmentActivity      mActivity;
    private ProgressFragmentUtil  mProgressFragmentUtil;
    private ProgressDialogUtil    mProgressDialogUtil;

    public UGCKitVideoRecord(Context context) {
        super(context);
        initDefault(context);
    }

    public UGCKitVideoRecord(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDefault(context);
    }

    public UGCKitVideoRecord(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDefault(context);
    }

    private void initDefault(Context context) {
        mActivity = (FragmentActivity) getContext();
        // ?????????SDK:TXUGCRecord
        VideoRecordSDK.getInstance().initSDK();
        // ????????????????????????
        VideoRecordSDK.getInstance().initRecordDraft(context);
        VideoRecordSDK.getInstance().setOnRestoreDraftListener(new VideoRecordSDK.OnRestoreDraftListener() {
            @Override
            public void onDraftProgress(long duration) {
                getRecordBottomLayout().updateProgress((int) duration);
                getRecordBottomLayout().getRecordProgressView().clipComplete();
            }

            @Override
            public void onDraftTotal(long duration) {
                getRecordRightLayout().setMusicIconEnable(false);
                getRecordRightLayout().setAspectIconEnable(false);

                float second = duration / 1000f;
                boolean enable = second >= UGCKitRecordConfig.getInstance().mMinDuration / 1000;
                getTitleBar().setVisible(enable, ITitleBarLayout.POSITION.RIGHT);
            }
        });

        VideoRecordSDK.getInstance().setVideoRecordListener(this);
        // ??????"?????????"
        getTitleBar().setVisible(false, ITitleBarLayout.POSITION.RIGHT);
        getTitleBar().setOnRightClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialogUtil.showProgressDialog();

                VideoRecordSDK.getInstance().stopRecord();
            }
        });

        // ??????"???????????????"?????????"??????"???"??????"???"??????"???
        getRecordRightLayout().setOnItemClickListener(this);

        // ??????"????????????"?????????"??????"???"?????????"???"?????????"???
        getRecordBottomLayout().setOnRecordButtonListener(this);
        getRecordBottomLayout().setOnDeleteLastPartListener(new RecordBottomLayout.OnDeleteLastPartListener() {
            @Override
            public void onUpdateTitle(boolean enable) {
                getTitleBar().setVisible(enable, ITitleBarLayout.POSITION.RIGHT);
            }

            @Override
            public void onReRecord() {
                getRecordRightLayout().setMusicIconEnable(true);
                getRecordRightLayout().setAspectIconEnable(true);
            }
        });

        // ??????"????????????"?????????
        getRecordMusicPannel().setOnMusicChangeListener(this);
        // ??????"????????????"?????????
        getSoundEffectPannel().setSoundEffectsSettingPannelListener(this);

        getScrollFilterView().setOnRecordFilterListener(this);

        TelephonyUtil.getInstance().initPhoneListener();
        mProgressDialogUtil = new ProgressDialogUtil(mActivity);

        UGCKitRecordConfig config = UGCKitRecordConfig.getInstance();
        config.mBeautyParams = new BeautyParams();

        // ??????????????????
        config.mBeautyParams.mBeautyStyle = 0;
        config.mBeautyParams.mBeautyLevel = 4;
        config.mBeautyParams.mWhiteLevel = 1;
        // ?????????????????????
        VideoRecordSDK.getInstance().initConfig(config);
        VideoRecordSDK.getInstance().updateBeautyParam(config.mBeautyParams);

        TXUGCRecord txugcRecord = VideoRecordSDK.getInstance().getRecorder();
        getBeautyPanel().setBeautyManager(txugcRecord.getBeautyManager());
        getBeautyPanel().setOnFilterChangeListener(new Beauty.OnFilterChangeListener() {
            @Override
            public void onChanged(Bitmap filterImage, int index) {
                getScrollFilterView().doTextAnimator(index);
            }
        });
        getBeautyPanel().setOnBeautyListener(new BeautyPanel.OnBeautyListener() {
            @Override
            public void onTabChange(TabInfo tabInfo, int position) {

            }

            @Override
            public boolean onClose() {
                getBeautyPanel().setVisibility(View.GONE);
                getRecordMusicPannel().setVisibility(View.GONE);
                getSoundEffectPannel().setVisibility(View.GONE);

                getRecordBottomLayout().setVisibility(View.VISIBLE);
                getRecordRightLayout().setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onClick(TabInfo tabInfo, int tabPosition, ItemInfo itemInfo, int itemPosition) {
                return false;
            }

            @Override
            public boolean onLevelChanged(TabInfo tabInfo, int tabPosition, ItemInfo itemInfo, int itemPosition, int beautyLevel) {
                return false;
            }
        });
    }

    @Override
    public void setOnRecordListener(OnRecordListener listener) {
        mOnRecordListener = listener;
    }

    @Override
    public void setOnMusicChooseListener(OnMusicChooseListener listener) {
        mOnMusicListener = listener;
    }

    @Override
    public void start() {
        // ????????????????????????
        VideoRecordSDK.getInstance().startCameraPreview(getRecordVideoView());
    }

    @Override
    public void stop() {
        Log.d(TAG, "stop");
        TelephonyUtil.getInstance().uninitPhoneListener();

        getRecordBottomLayout().getRecordButton().pauseRecordAnim();
        getRecordBottomLayout().closeTorch();
        // ????????????????????????
        VideoRecordSDK.getInstance().stopCameraPreview();
        // ????????????
        VideoRecordSDK.getInstance().pauseRecord();
    }

    @Override
    public void release() {
        Log.d(TAG, "release");
        getRecordBottomLayout().getRecordProgressView().release();
        // ????????????
        VideoRecordSDK.getInstance().releaseRecord();

        UGCKitRecordConfig.getInstance().clear();
        // ??????TXUGCRecord???????????????????????????????????????
        getBeautyPanel().clear();

        VideoRecordSDK.getInstance().setVideoRecordListener(null);
        getBeautyPanel().setOnFilterChangeListener(null);
    }

    @Override
    public void screenOrientationChange() {
        Log.d(TAG, "screenOrientationChange");
        VideoRecordSDK.getInstance().stopCameraPreview();

        VideoRecordSDK.getInstance().pauseRecord();

        VideoRecordSDK.getInstance().startCameraPreview(getRecordVideoView());
    }

    @Override
    public void setRecordMusicInfo(@NonNull MusicInfo musicInfo) {
        if (musicInfo != null) {
            Log.d(TAG, "music name:" + musicInfo.name + ", path:" + musicInfo.path);
        }
        getRecordBottomLayout().setVisibility(View.INVISIBLE);
        getRecordRightLayout().setVisibility(View.INVISIBLE);

        TXUGCRecord record = VideoRecordSDK.getInstance().getRecorder();
        if (record != null) {
            long duration = record.setBGM(musicInfo.path);
            musicInfo.duration = duration;
            Log.d(TAG, "music duration:" + musicInfo.duration);
        }
        // ??????????????????
        RecordMusicManager.getInstance().setRecordMusicInfo(musicInfo);
        // ????????????Pannel
        getRecordMusicPannel().setMusicInfo(musicInfo);
        getRecordMusicPannel().setVisibility(View.VISIBLE);

        // ????????????
        RecordMusicManager.getInstance().startPreviewMusic();
    }

    @Override
    public void backPressed() {
        Log.d(TAG, "backPressed");
        // ???????????????????????????"???????????????"
        if (VideoRecordSDK.getInstance().getRecordState() == VideoRecordSDK.STATE_STOP) {
            if (mOnRecordListener != null) {
                mOnRecordListener.onRecordCanceled();
            }
            return;
        }
        // ????????????????????????????????????????????????
        if (VideoRecordSDK.getInstance().getRecordState() == VideoRecordSDK.STATE_START) {
            VideoRecordSDK.getInstance().pauseRecord();
        }

        int size = VideoRecordSDK.getInstance().getPartManager().getPartsPathList().size();
        if (size == 0) {
            if (mOnRecordListener != null) {
                mOnRecordListener.onRecordCanceled();
            }
            return;
        }

        showGiveupRecordDialog();
    }

    /**
     * ???????????????????????????
     */
    private void showGiveupRecordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alertDialog = builder.setTitle(getResources().getString(R.string.ugckit_cancel_record)).setCancelable(false).setMessage(R.string.ugckit_confirm_cancel_record_content)
                .setPositiveButton(R.string.ugckit_give_up, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.dismiss();

                        VideoRecordSDK.getInstance().deleteAllParts();

                        if (mOnRecordListener != null) {
                            mOnRecordListener.onRecordCanceled();
                        }
                        return;
                    }
                })
                .setNegativeButton(getResources().getString(R.string.ugckit_wrong_click), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        alertDialog.show();
    }

    /**
     * ????????????????????????
     */
    @Override
    public void onRecordStart() {
        getRecordRightLayout().setVisibility(View.INVISIBLE);
        getRecordBottomLayout().startRecord();
        // ????????????????????????????????????
        getRecordRightLayout().setMusicIconEnable(false);
        // ?????????????????????????????????
        getRecordRightLayout().setAspectIconEnable(false);

        // ??????/????????????
        int retCode = VideoRecordSDK.getInstance().startRecord();
        if (retCode == VideoRecordSDK.START_RECORD_FAIL) { //?????????????????????????????????????????????????????????
            getRecordBottomLayout().getRecordButton().pauseRecordAnim();
            return;
        }

        AudioFocusManager.getInstance().setAudioFocusListener(new AudioFocusManager.OnAudioFocusListener() {
            @Override
            public void onAudioFocusChange() {
                VideoRecordSDK.getInstance().pauseRecord();
            }
        });
        AudioFocusManager.getInstance().requestAudioFocus();
    }

    /**
     * ????????????????????????
     */
    @Override
    public void onRecordPause() {
        Log.d(TAG, "onRecordPause");
        getRecordRightLayout().setVisibility(View.VISIBLE);
        getRecordBottomLayout().pauseRecord();

        VideoRecordSDK.getInstance().pauseRecord();
        RecordMusicManager.getInstance().pauseMusic();

        AudioFocusManager.getInstance().abandonAudioFocus();
    }

    /**
     * ????????????
     */
    @Override
    public void onTakePhoto() {
        PhotoSoundPlayer.playPhotoSound();

        VideoRecordSDK.getInstance().takePhoto(new RecordModeView.OnSnapListener() {
            @Override
            public void onSnap(Bitmap bitmap) {
                getSnapshotView().showSnapshotAnim(bitmap);
            }
        });
    }

    @Override
    public void onDeleteParts(int partsSize, long duration) {

    }

    @Override
    public void onShowBeautyPanel() {
        // ?????????????????????
        getRecordBottomLayout().setVisibility(View.GONE);
        // ?????????????????????
        getRecordRightLayout().setVisibility(View.GONE);
        // ????????????Pannel
        getBeautyPanel().setVisibility(View.VISIBLE);
    }

    /**
     * ?????????????????????"??????"
     */
    @Override
    public void onShowMusicPanel() {
        boolean isChooseMusicFlag = RecordMusicManager.getInstance().isChooseMusic();
        if (isChooseMusicFlag) {
            // ?????????????????????
            getRecordBottomLayout().setVisibility(View.GONE);
            // ?????????????????????
            getRecordRightLayout().setVisibility(View.GONE);
            // ????????????Pannel
            getRecordMusicPannel().setVisibility(View.VISIBLE);

            RecordMusicManager.getInstance().startMusic();
        } else {
            if (mOnMusicListener != null) {
                mOnMusicListener.onChooseMusic(UGCKitRecordConfig.getInstance().musicInfo.position);
            }
        }
    }

    @Override
    public void onShowSoundEffectPanel() {
        // ?????????????????????
        getRecordBottomLayout().setVisibility(View.GONE);
        // ?????????????????????
        getRecordRightLayout().setVisibility(View.GONE);
        // ????????????Pannel
        getSoundEffectPannel().setVisibility(View.VISIBLE);
    }

    @Override
    public void onAspectSelect(int aspectType) {
        UGCKitRecordConfig.getInstance().mAspectRatio = aspectType;
        VideoRecordSDK.getInstance().updateAspectRatio();
    }

    /************************************   ??????Pannel???????????? Begin  ********************************************/
    @Override
    public void onMicVolumeChanged(float volume) {
        TXUGCRecord record = VideoRecordSDK.getInstance().getRecorder();
        if (record != null) {
            record.setMicVolume(volume);
        }
    }

    @Override
    public void onClickVoiceChanger(int type) {
        TXUGCRecord record = VideoRecordSDK.getInstance().getRecorder();
        if (record != null) {
            record.setVoiceChangerType(type);
        }
    }

    @Override
    public void onClickReverb(int type) {
        TXUGCRecord record = VideoRecordSDK.getInstance().getRecorder();
        if (record != null) {
            record.setReverb(type);
        }
    }

    /************************************   ??????Pannel???????????? End    ********************************************/

    /************************************   ??????Pannel???????????? Begin  ********************************************/
    @Override
    public void onMusicVolumChanged(float volume) {
        TXUGCRecord record = VideoRecordSDK.getInstance().getRecorder();
        if (record != null) {
            record.setBGMVolume(volume);
        }
    }

    /**
     * ??????????????????
     *
     * @param startTime
     * @param endTime
     */
    @Override
    public void onMusicTimeChanged(long startTime, long endTime) {
        MusicInfo musicInfo = RecordMusicManager.getInstance().getMusicInfo();
        musicInfo.startTime = startTime;
        musicInfo.endTime = endTime;

        RecordMusicManager.getInstance().startPreviewMusic();
    }

    /**
     * ??????"??????Pannel"?????????</p>
     * 1???????????????Pannel</p>
     * 2?????????????????????
     */
    @Override
    public void onMusicSelect() {
        getRecordBottomLayout().setVisibility(View.VISIBLE);
        getRecordRightLayout().setVisibility(View.VISIBLE);
        // ????????????BGM???????????????????????????????????????????????????????????????
        getRecordRightLayout().setSoundEffectsEnabled(false);

        getRecordMusicPannel().setVisibility(View.GONE);

        // ??????????????????
        RecordMusicManager.getInstance().stopPreviewMusic();
    }

    /**
     * ??????"??????Pannel"???????????????
     */
    @Override
    public void onMusicReplace() {
        if (mOnMusicListener != null) {
            mOnMusicListener.onChooseMusic(UGCKitRecordConfig.getInstance().musicInfo.position);
        }
    }

    /**
     * ??????"??????Pannel"??????????????????
     */
    @Override
    public void onMusicDelete() {
        showDeleteMusicDialog();
    }

    private void showDeleteMusicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alertDialog = builder.setTitle(getResources().getString(R.string.ugckit_tips)).setCancelable(false).setMessage(R.string.ugckit_delete_bgm_or_not)
                .setPositiveButton(R.string.ugckit_confirm_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.dismiss();

                        RecordMusicManager.getInstance().deleteMusic();
                        // ????????????BGM???????????????????????????????????????????????????????????????
                        getRecordRightLayout().setSoundEffectIconEnable(true);

//                        getRecordMusicPannel().setMusicName("");
                        getRecordMusicPannel().setVisibility(View.GONE);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.ugckit_btn_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        alertDialog.show();
    }

    /************************************   ??????Pannel???????????? End    ********************************************/

    @Override
    public void onSingleClick(float x, float y) {
        getBeautyPanel().setVisibility(View.GONE);
        getRecordMusicPannel().setVisibility(View.GONE);
        getSoundEffectPannel().setVisibility(View.GONE);

        getRecordBottomLayout().setVisibility(View.VISIBLE);
        getRecordRightLayout().setVisibility(View.VISIBLE);
        TXUGCRecord record = VideoRecordSDK.getInstance().getRecorder();
        if (record != null) {
            record.setFocusPosition(x, y);
        }
    }

    @Override
    public void onRecordProgress(long milliSecond) {
        getRecordBottomLayout().updateProgress(milliSecond);

        float second = milliSecond / 1000f;
        boolean enable = second >= UGCKitRecordConfig.getInstance().mMinDuration / 1000;
        getTitleBar().setVisible(enable, ITitleBarLayout.POSITION.RIGHT);
    }

    @Override
    public void onRecordEvent() {
        getRecordBottomLayout().getRecordProgressView().clipComplete();
    }

    @Override
    public void onRecordComplete(@NonNull TXRecordCommon.TXRecordResult result) {
        LogReport.getInstance().uploadLogs(LogReport.ELK_ACTION_VIDEO_RECORD, result.retCode, result.descMsg);

        if (result.retCode >= 0) {
            mProgressDialogUtil.dismissProgressDialog();
            boolean editFlag = UGCKitRecordConfig.getInstance().mIsNeedEdit;
            if (editFlag) {
                // ????????????????????????????????????????????????????????????
                startPreprocess(result.videoPath);
            } else {
                // ?????????????????????????????????????????????????????????????????????
                if (mOnRecordListener != null) {
                    UGCKitResult ugcKitResult = new UGCKitResult();
                    String outputPath = VideoRecordSDK.getInstance().getRecordVideoPath();
                    ugcKitResult.errorCode = result.retCode;
                    ugcKitResult.descMsg = result.descMsg;
                    ugcKitResult.outputPath = outputPath;
                    ugcKitResult.coverPath = result.coverPath;
                    mOnRecordListener.onRecordCompleted(ugcKitResult);
                }
            }
        }
    }

    private void startPreprocess(String videoPath) {
        mProgressFragmentUtil = new ProgressFragmentUtil(mActivity);
        mProgressFragmentUtil.showLoadingProgress(new ProgressFragmentUtil.IProgressListener() {
            @Override
            public void onStop() {
                mProgressFragmentUtil.dismissLoadingProgress();

                ProcessKit.getInstance().stopProcess();
            }
        });

        loadVideoInfo(videoPath);
    }

    /**
     * ??????????????????
     *
     * @param videoPath
     */
    private void loadVideoInfo(final String videoPath) {
        TXVideoEditConstants.TXVideoInfo info = TXVideoInfoReader.getInstance(UGCKit.getAppContext()).getVideoFileInfo(videoPath);
        if (info == null) {
            DialogUtil.showDialog(getContext(), getResources().getString(R.string.ugckit_video_preprocess_activity_edit_failed), getResources().getString(R.string.ugckit_does_not_support_android_version_below_4_3), null);
        } else {
            // ????????????????????????
            VideoEditerSDK.getInstance().initSDK();
            VideoEditerSDK.getInstance().getEditer().setVideoPath(videoPath);
            VideoEditerSDK.getInstance().setTXVideoInfo(info);
            VideoEditerSDK.getInstance().setCutterStartTime(0, info.duration);

            ProcessKit.getInstance().setOnUpdateUIListener(new OnUpdateUIListener() {
                @Override
                public void onUIProgress(float progress) {
                    mProgressFragmentUtil.updateGenerateProgress((int) (progress * 100));
                }

                @Override
                public void onUIComplete(int retCode, String descMsg) {
                    // ??????UI??????
                    mProgressFragmentUtil.dismissLoadingProgress();
                    if (mOnRecordListener != null) {
                        UGCKitResult ugcKitResult = new UGCKitResult();
                        ugcKitResult.errorCode = retCode;
                        ugcKitResult.descMsg = descMsg;
                        ugcKitResult.outputPath = videoPath;
                        mOnRecordListener.onRecordCompleted(ugcKitResult);
                    }
                }

                @Override
                public void onUICancel() {
                    // ??????Activity
                    if (mOnRecordListener != null) {
                        mOnRecordListener.onRecordCanceled();
                    }
                }
            });
            // ?????????????????????
            ProcessKit.getInstance().startProcess();
        }
    }

    @Override
    public void setConfig(UGCKitRecordConfig config) {
        VideoRecordSDK.getInstance().setConfig(config);
        // ???????????????/????????????????????????
        getRecordBottomLayout().initDuration();
        // ???????????????????????????
        getRecordBottomLayout().getRecordButton().setCurrentRecordMode(UGCKitRecordConfig.getInstance().mRecordMode);
        // ??????????????????UI
        getRecordRightLayout().setAspect(config.mAspectRatio);
    }

    @Override
    public void setEditVideoFlag(boolean enable) {
        UGCKitRecordConfig.getInstance().mIsNeedEdit = enable;
    }

}
