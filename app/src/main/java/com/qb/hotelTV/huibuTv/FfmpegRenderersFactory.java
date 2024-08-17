package com.qb.hotelTV.huibuTv;

import android.content.Context;
import android.os.Handler;
import android.util.Log;


import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;

import java.util.ArrayList;

public class FfmpegRenderersFactory extends DefaultRenderersFactory {

    public FfmpegRenderersFactory(Context context) {
        super(context);
        setExtensionRendererMode(EXTENSION_RENDERER_MODE_ON);
        Log.d("FfmpegAudioRenderer", "Custom FfmpegAudioRenderer is being used");
    }

    @Override
    protected void buildAudioRenderers(Context context,
                                       int extensionRendererMode,
                                       MediaCodecSelector mediaCodecSelector,
                                       boolean enableDecoderFallback,
                                       AudioSink audioSink, Handler eventHandler,
                                       AudioRendererEventListener eventListener,
                                       ArrayList<Renderer> out) {
        out.add(new FfmpegAudioRenderer(eventHandler, eventListener, audioSink));

        super.buildAudioRenderers(
                context,
                extensionRendererMode,
                mediaCodecSelector,
                enableDecoderFallback,
                audioSink, eventHandler,
                eventListener,
                out
        );
    }
}

