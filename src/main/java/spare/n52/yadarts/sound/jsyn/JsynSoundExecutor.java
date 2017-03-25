
package spare.n52.yadarts.sound.jsyn;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.data.FloatSample;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.VariableRateDataReader;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.jsyn.unitgen.VariableRateStereoReader;
import com.jsyn.util.SampleLoader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spare.n52.yadarts.sound.SoundExecutor;
import spare.n52.yadarts.sound.SoundId;

/**
 *
 * @author <a href="mailto:m.rieke@52north.org">Matthes Rieke</a>
 */
public class JsynSoundExecutor implements SoundExecutor {
    
    private static final Logger LOG = LoggerFactory.getLogger(JsynSoundExecutor.class);
    
    private Synthesizer synth;
    private LineOut lineOut;
    private VariableRateMonoReader monoPlayer;
    private VariableRateStereoReader stereoPlayer;
    private String soundPackageName;
    
    @Override
    public void init(String soundPackageName) {
        LOG.info("initing with sound package: "+soundPackageName);
        this.soundPackageName = soundPackageName;
        
        synth = JSyn.createSynthesizer();
        synth.add(lineOut = new LineOut());
        
        monoPlayer = new VariableRateMonoReader();
        monoPlayer.output.connect(0, lineOut.input, 0);
        synth.add(monoPlayer);
        
        stereoPlayer = new VariableRateStereoReader();
        stereoPlayer.output.connect(0, lineOut.input, 0);
        stereoPlayer.output.connect(1, lineOut.input, 1);
        synth.add(stereoPlayer);
        
        // Start synthesizer using default stereo output at 44100 Hz.
        synth.start();
        
        // We only need to start the LineOut. It will pull data from the
        // sample player.
        lineOut.start();
    }
    
    @Override
    public void add(SoundId id) {
        FloatSample sample;
        try {
            String res = "/sounds/"+ soundPackageName + "/" + id.name().toLowerCase() + ".wav";
            LOG.info("Loading sound: "+res);
            InputStream is = getClass().getResourceAsStream(res);
            if (is == null) {
                return;
            }
            sample = SampleLoader.loadFloatSample(is);
        } catch (IOException ex) {
            LOG.warn("Could not load sound: ", ex.getMessage());
            return;
        }
        
        VariableRateDataReader samplePlayer;
        switch (sample.getChannelsPerFrame()) {
            case 1:
                samplePlayer = monoPlayer;
                break;
            case 2:
                samplePlayer = stereoPlayer;
                break;
            default:
                return;
        }
        
        samplePlayer.rate.set(sample.getFrameRate());
        
        samplePlayer.dataQueue.queue(sample);
    }
    
    @Override
    public void add(List<SoundId> list) {
        for (SoundId soundId : list) {
            add(soundId);
        }
    }
    
    @Override
    public void shutdown() {
        synth.stop();
    }
    
}
