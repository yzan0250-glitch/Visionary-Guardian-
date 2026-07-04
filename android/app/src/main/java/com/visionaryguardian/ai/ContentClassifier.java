package com.visionaryguardian.ai;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.CompatibilityList;
import org.tensorflow.lite.gpu.GpuDelegate;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class ContentClassifier {

    private Interpreter tflite;
    private final int INPUT_SIZE = 224; // 标准轻量级视觉模型（如MobileNet）的输入分辨率
    private ByteBuffer imgData;
    private float[][] outputScores;

    public ContentClassifier(Context context) {
        try {
            // 1. 硬件加速策略：优先检测并启用手机的 GPU / NPU 硬件加速，降功耗防止手机发热
            Interpreter.Options options = new Interpreter.Options();
            CompatibilityList compatList = new CompatibilityList();
            if (compatList.isDelegateSupportedOnThisDevice()) {
                GpuDelegate.Options delegateOptions = compatList.getBestOptionsForThisDevice();
                GpuDelegate gpuDelegate = new GpuDelegate(delegateOptions);
                options.addDelegate(gpuDelegate);
            }
            
            // 2. 加载本地打包装进 App 的量化轻量级模型文件（后期我们会把防线模型命名为 guardian_model.tflite）
            tflite = new Interpreter(loadModelFile(context, "guardian_model.tflite"), options);
            
            // 3. 预分配内存块（RGBA三通道，4字节浮点数），避免在循环截屏中反复创建对象导致内存抖动
            imgData = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * 3);
            imgData.order(ByteOrder.nativeOrder());
            
            // 输出分类结果：假设模型输出两个概率（[0]代表安全，[1]代表有害）
            outputScores = new float[1][2];
            
        } catch (Exception e) {
            e.clear();
            e.printStackTrace();
        }
    }

    /**
     * 核心判定方法：输入当前屏幕帧的 Bitmap，毫秒级输出是否包含违规视觉内容
     */
    public boolean isHarmfulContent(Bitmap bitmap) {
        if (tflite == null) return false;

        // 1. 将截图快速缩放到模型需要的 224x224 分辨率
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true);
        
        // 2. 将 Bitmap 的像素归一化并写入预分配的 ByteBuffer
        convertBitmapToByteBuffer(scaledBitmap);
        scaledBitmap.recycle(); // 及时释放缩放后的临时位图

        // 3. 运行本地 NPU 推理
        tflite.run(imgData, outputScores);

        // 4. 解析结果：如果[1]（有害内容分数）大于 0.85（85%置信度），判定为违规
        float harmfulScore = outputScores[0][1];
        return harmfulScore > 0.85f;
    }

    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) return;
        imgData.rewind();
        int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        
        int pixel = 0;
        for (int i = 0; i < INPUT_SIZE; ++i) {
            for (int j = 0; j < INPUT_SIZE; ++j) {
                final int val = intValues[pixel++];
                // 提取 RGB 通道并归一化到 0~1.0 之间
                imgData.putFloat((((val >> 16) & 0xFF) / 255.0f));
                imgData.putFloat((((val >> 8) & 0xFF) / 255.0f));
                imgData.putFloat(((val & 0xFF) / 255.0f));
            }
        }
    }

    private ByteBuffer loadModelFile(Context context, String modelName) throws Exception {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelName);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public void close() {
        if (tflite != null) {
            tflite.close();
            tflite = null;
        }
    }
}