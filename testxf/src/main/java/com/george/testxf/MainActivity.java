package com.george.testxf;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.george.testxf.util.FucUtil;
import com.george.testxf.util.JsonParser;
import com.george.testxf.util.XmlParser;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "GeorgeTag";
    private static boolean mscInitialize = false; // 是否初始化过

    private Toast mToast;

    private SpeechSynthesizer mTts; // 语音合成对象
    private Button btn_yyhc; // 语音合成
    private Button btn_init; // SDK初始化
    private Button btn_yyml; // 语音命令
    private Button btn_gxcd; // 更新词典
    private Button btn_gxcd2; // 更新词典

    // 本地语法构建路径
    private String grmPath;
    // 语音识别对象
    private SpeechRecognizer mAsr;
    // 本地词典
    private String mLocalLexicon = null;
    // 本地语法文件
    private String mLocalGrammar = null;
    // 识别引擎类型
    private String mEngineType = null;
    // 语法类型
    private final String GRAMMAR_TYPE_BNF = "bnf";
    int ret = 0;// 函数调用返回值
    // 返回结果格式，支持：xml,json
    private String mResultType = "json";
    private TextView tv_result;
    private boolean isUpdate = false;
    // 语音唤醒对象
    private VoiceWakeuper mIvw;
    // 唤醒结果内容
    private String resultString = "";
    private String keep_alive = "1"; // 是否持续激活: 0-否 1-是
    private String ivwNetMode = "0"; // 是否开启网络优化, 0-不开启 1-开启,允许上传优化数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_result = findViewById(R.id.tv_result);

        // 引擎初始化
        btn_init = findViewById(R.id.btn_init);
        btn_init.setOnClickListener(this);

        // 语音合成
        btn_yyhc = findViewById(R.id.btn_yyhc);
        btn_yyhc.setOnClickListener(this);

        // 语音命令
        btn_yyml = findViewById(R.id.btn_yyml);
        btn_yyml.setOnClickListener(this);

        // 更新词典
        btn_gxcd = findViewById(R.id.btn_gxcd);
        btn_gxcd.setOnClickListener(this);

        // 更新词典
        btn_gxcd2 = findViewById(R.id.btn_gxcd2);
        btn_gxcd2.setOnClickListener(this);

        // 语音唤醒
        findViewById(R.id.btn_yyhx).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_init) { // 讯飞语音初始化
            // 校验是否有播放器的权限,有的话初始化讯飞语音SDK
            mPermissionResult.launch(Manifest.permission.RECORD_AUDIO);
        }
        if (v.getId() == R.id.btn_yyhc) { // 语音合成
            // 初始化合成对象
            if (mTts == null) {
                mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
            } else {
                int code2 = mTts.startSpeaking("自定义语音合成OK,等待项目继承", mTtsListener);
                if (code2 != ErrorCode.SUCCESS) {
                    showTip("语音合成失败,错误码: " + code2 + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
                }
            }
        }
        if (v.getId() == R.id.btn_yyml) { // 语音命令
            // 本地语法构建路径
            grmPath = getExternalFilesDir("msc").getAbsolutePath() + "/test";
            // 初始化识别对象
            mAsr = SpeechRecognizer.createRecognizer(this, mInitListener);
            if (mAsr == null) {
                Log.e(TAG, "masr is null");
            }

            mLocalGrammar = FucUtil.readFile(this, "call.bnf", "utf-8");
            mEngineType = SpeechConstant.TYPE_LOCAL;

            if (!isUpdate) { // 如果没有构建过语法,则先构建语法
                // 构建本地语法
                setLocalGrammar();
            }

            // 设置语音命令参数
            if (!setAsrParam()) {
                showTip("请先构建语法。");
                return;
            }

            // 开始语音指令监听
            ret = mAsr.startListening(mRecognizerListener);
            if (ret != ErrorCode.SUCCESS) {
                showTip("识别失败,错误码: " + ret + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }
        }

        if (v.getId() == R.id.btn_gxcd) { // 更新词典
            // 初始化语法、命令词
            mLocalLexicon = "张海洋\n宋丹丹\n赵本山";
            String mContent = new String(mLocalLexicon);

            mAsr.setParameter(SpeechConstant.PARAMS, null);
            // 设置引擎类型
            mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置资源路径
            mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getAsrResourcePath());
            //使用8k音频的时候请解开注释
//				mAsr.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
            // 设置语法构建路径
            mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
            // 设置语法名称
            mAsr.setParameter(SpeechConstant.GRAMMAR_LIST, "call");
            // 设置文本编码格式
            mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
            ret = mAsr.updateLexicon("contact", mContent, lexiconListener);
            if (ret != ErrorCode.SUCCESS) {
                showTip("更新词典失败,错误码：" + ret + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }
            isUpdate = true;
        }

        if (v.getId() == R.id.btn_gxcd2) { // 更新词典2
            mAsr.setParameter(SpeechConstant.PARAMS, null);
            // 设置引擎类型
            mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置资源路径
            mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getAsrResourcePath());
            //使用8k音频的时候请解开注释
//				mAsr.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
            // 设置语法构建路径
            mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
            // 设置语法名称
            mAsr.setParameter(SpeechConstant.GRAMMAR_LIST, "call");
            // 设置文本编码格式
            mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");

            // 连续的更新词典,只有第一次会成功,
            String move = "发短信\n打电话\n发微信";
            ret = mAsr.updateLexicon("callPhone", move, lexiconListener);
            if (ret != ErrorCode.SUCCESS) {
                showTip("更新词典失败,错误码：" + ret + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }

            isUpdate = true;
        }

        if (v.getId() == R.id.btn_yyhx) { // 语音唤醒

            //非空判断，防止因空指针使程序崩溃
            mIvw = VoiceWakeuper.getWakeuper();
            if (mIvw != null) {
                tv_result.setText(resultString);
                // 清空参数
                mIvw.setParameter(SpeechConstant.PARAMS, null);
                // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
                mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:" + 1450);
                // 设置唤醒模式
                mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
                // 设置持续进行唤醒
                mIvw.setParameter(SpeechConstant.KEEP_ALIVE, keep_alive);
                // 设置闭环优化网络模式
                mIvw.setParameter(SpeechConstant.IVW_NET_MODE, ivwNetMode);
                // 设置唤醒资源路径
                mIvw.setParameter(SpeechConstant.IVW_RES_PATH, getWakeuperResource());
                // 设置唤醒录音保存路径，保存最近一分钟的音频
                mIvw.setParameter(SpeechConstant.IVW_AUDIO_PATH,
                        getExternalFilesDir("msc").getAbsolutePath() + "/ivw.wav");
                mIvw.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
                // 如有需要，设置 NOTIFY_RECORD_DATA 以实时通过 onEvent 返回录音音频流字节
                //mIvw.setParameter( SpeechConstant.NOTIFY_RECORD_DATA, "1" );
                // 启动唤醒
                /*	mIvw.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");*/

                mIvw.startListening(mWakeuperListener);
            } else {
                showTip("唤醒未初始化");
            }
        }

    }

    /**
     * 设置语音命令参数
     */
    private boolean setAsrParam() {
        boolean result = false;
        // 清空参数
        mAsr.setParameter(SpeechConstant.PARAMS, null);
        // 设置识别引擎
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);

        // 设置本地识别资源
        mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getAsrResourcePath());
        // 设置语法构建路径
        mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
        // 设置返回结果格式
        mAsr.setParameter(SpeechConstant.RESULT_TYPE, mResultType);
        // 设置本地识别使用语法id
        mAsr.setParameter(SpeechConstant.LOCAL_GRAMMAR, "call");
        // 设置识别的门限值
        mAsr.setParameter(SpeechConstant.MIXED_THRESHOLD, "30");
        // 使用8k音频的时候请解开注释
//			mAsr.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
        result = true;
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mAsr.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH,
                getExternalFilesDir("msc").getAbsolutePath() + "/asr.wav");
        return result;
    }

    /**
     * 设置本地语法
     */
    private void setLocalGrammar() {
        mAsr.setParameter(SpeechConstant.PARAMS, null);
        // 设置文本编码格式
        mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        // 设置引擎类型
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置语法构建路径
        mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
        //使用8k音频的时候请解开注释
//					mAsr.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
        // 设置资源路径
        mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getAsrResourcePath());
        //
        ret = mAsr.buildGrammar(GRAMMAR_TYPE_BNF, mLocalGrammar, grammarListener);
        if (ret != ErrorCode.SUCCESS) {
            showTip("语法构建失败,错误码：" + ret + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
        }
    }

    /**
     * 初始化讯飞语音SDK
     * @param context
     */
    public static void initializeMsc(Context context){
        if (mscInitialize) return;
        // 应用程序入口处调用,避免手机内存过小,杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
        // 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
        // 参数间使用“,”分隔。
        // 设置你申请的应用appid
        // 注意： appid 必须和下载的SDK保持一致，否则会出现10407错误
        StringBuffer param = new StringBuffer();
        param.append("appid=" + context.getString(R.string.app_id));
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(context, param.toString());
        mscInitialize = true;

        Toast.makeText(context, "语音SDK初始化完成", Toast.LENGTH_LONG).show();
    }

    /**
     * 初始化语音唤醒对象
     * @param context
     */
    public void initializeWakeuper(Context context) {
        // 初始化唤醒对象
        mIvw = VoiceWakeuper.createWakeuper(context, null);
    }

    /**
     * 校验应用权限并开始初始化SDK
     */
    private ActivityResultLauncher<String> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), result -> {
                if (result) {
                    // 初始化讯飞语音SDK
                    initializeMsc(MainActivity.this);
                    // 初始化语音唤醒对象
                    initializeWakeuper(MainActivity.this);
                }
            }
    );

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码：" + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里

                // 设置参数
                setParam();
                int code2 = mTts.startSpeaking("安全头盔佩戴异常,请及时处理", mTtsListener);
                if (code2 != ErrorCode.SUCCESS) {
                    showTip("语音合成失败,错误码: " + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
                }
            }
        }
    };

    /**
     * 参数设置
     */
    private void setParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        //设置合成
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_XTTS);
        //设置发音人资源路径
        mTts.setParameter(ResourceUtil.TTS_RES_PATH, getXttsResourcePath());
        //设置发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");

        //mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY,"1");//支持实时音频流抛出，仅在synthesizeToUri条件下支持
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "50");
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, AudioManager.STREAM_MUSIC+"");
        //	mTts.setParameter(SpeechConstant.STREAM_TYPE, AudioManager.STREAM_MUSIC+"");

        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,
                getExternalFilesDir("msc").getAbsolutePath() + "/tts.wav");
    }

    /**
     * 获取发音人资源路径
     * @return
     */
    private String getXttsResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        String type = "xtts";
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, type + "/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, type + "/" + "xiaoyan.jet"));
        return tempBuffer.toString();
    }

    //获取识别资源路径
    private String getAsrResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        //识别通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "asr/common.jet"));
        return tempBuffer.toString();
    }

    /**
     * 获取语音唤醒资源文件
     * @return
     */
    private String getWakeuperResource() {
        final String resPath = ResourceUtil.generateResourcePath(MainActivity.this, ResourceUtil.RESOURCE_TYPE.assets, "ivw/" + getString(R.string.app_id) + ".jet");
        Log.d(TAG, "resPath: " + resPath);
        return resPath;
    }

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            //showTip("开始播放");
            Log.d(TAG, "开始播放：" + System.currentTimeMillis());
        }

        @Override
        public void onSpeakPaused() {
            showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            showTip("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {

            // 合成进度
            Log.d(TAG, "合成进度: " + percent);
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            Log.d(TAG, "播放进度: " + percent);
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                showTip("播放完成");
            } else {
                showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                String sid = obj.getString(SpeechEvent.KEY_EVENT_AUDIO_URL);
                Log.d(TAG, "session id =" + sid);
            }

            //实时音频流输出参考
			/*if (SpeechEvent.EVENT_TTS_BUFFER == eventType) {
				byte[] buf = obj.getByteArray(SpeechEvent.KEY_EVENT_TTS_BUFFER);
				Log.e("MscSpeechLog", "buf is =" + buf);
			}*/
        }
    };


    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码：" + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }
        }
    };

    /**
     * 构建语法监听器。
     */
    private GrammarListener grammarListener = new GrammarListener() {
        @Override
        public void onBuildFinish(String grammarId, SpeechError error) {
            if (error == null) {
                showTip("语法构建成功：" + grammarId);
            } else {
                showTip("语法构建失败,错误码：" + error.getErrorCode());
            }
        }
    };

    /**
     * 识别监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
            if (null != result && !TextUtils.isEmpty(result.getResultString())) {
                Log.d(TAG, "recognizer result：" + result.getResultString());
                String text = "";
                if (mResultType.equals("json")) {
                    text = JsonParser.parseGrammarResult(result.getResultString(), mEngineType);
                } else if (mResultType.equals("xml")) {
                    text = XmlParser.parseNluResult(result.getResultString());
                } else {
                    text = result.getResultString();
                }

                tv_result.setText(text);

            } else {
                Log.d(TAG, "recognizer result : null");
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            showTip("onError Code：" + error.getErrorCode());
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }

    };

    /**
     * 更新词典监听器。
     */
    private LexiconListener lexiconListener = new LexiconListener() {
        @Override
        public void onLexiconUpdated(String lexiconId, SpeechError error) {
            if (error == null) {
                Log.d(TAG, "词典更新成功");
                showTip("词典更新成功");
            } else {
                Log.e(TAG, "词典更新失败, 错误码:" + error.getErrorCode());
                showTip("词典更新失败,错误码：" + error.getErrorCode());
            }
        }
    };

    /**
     * 唤醒监听器
     */
    private WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onResult(WakeuperResult result) {
            Log.d(TAG, "onResult");
            try {
                String text = result.getResultString();
                JSONObject object;
                object = new JSONObject(text);
                StringBuffer buffer = new StringBuffer();
                buffer.append("【RAW】 " + text);
                buffer.append("\n");
                buffer.append("【操作类型】" + object.optString("sst"));
                buffer.append("\n");
                buffer.append("【唤醒词id】" + object.optString("id"));
                buffer.append("\n");
                buffer.append("【得分】" + object.optString("score"));
                buffer.append("\n");
                buffer.append("【前端点】" + object.optString("bos"));
                buffer.append("\n");
                buffer.append("【尾端点】" + object.optString("eos"));
                resultString = buffer.toString();
            } catch (JSONException e) {
                resultString = "结果解析出错";
                e.printStackTrace();
            }
            tv_result.setText(resultString);
        }

        @Override
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onBeginOfSpeech() {
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
            switch (eventType) {
                // EVENT_RECORD_DATA 事件仅在 NOTIFY_RECORD_DATA 参数值为 真 时返回
                case SpeechEvent.EVENT_RECORD_DATA:
                    final byte[] audio = obj.getByteArray(SpeechEvent.KEY_EVENT_RECORD_DATA);
                    Log.i(TAG, "ivw audio length: " + audio.length);
                    break;
            }
        }

        @Override
        public void onVolumeChanged(int volume) {

        }
    };

    private void showTip(final String str) {
        runOnUiThread(() -> {
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT);
            mToast.show();
        });
    }
}