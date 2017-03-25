package spare.n52.yadarts.sound;

import java.util.List;

/**
 *
 * @author <a href="mailto:m.rieke@52north.org">Matthes Rieke</a>
 */
public interface SoundExecutor {
    
    void init(String soundPackageName);

    void add(SoundId id);

    void add(List<SoundId> list);

    void shutdown();
    
}
