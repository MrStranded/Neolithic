package load;

import constants.ResourcePathConstants;
import engine.parser.utils.Logger;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class AssetResolver {

    public static String getTexturePath(String partialPath) {
        for (String mod : getOrderedMods()) {
            String path = ResourcePathConstants.MOD_FOLDER
                    + mod + "/"
                    + ResourcePathConstants.TEXTURE_FOLDER
                    + partialPath;
            File pathFile = new File(path);

            if (pathFile.exists() && pathFile.isFile()) {
                return path;
            }
        }

        Logger.error("Did not find texture '" + partialPath + "' in any mod asset folder");
        return partialPath;
    }

    private static List<String> getOrderedMods() {
        List<String> mods = ModOrderLoader.loadMods();
        Collections.reverse(mods);
        return mods;
    }

}
