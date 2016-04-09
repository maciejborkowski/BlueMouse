package lex.bluemouse.action;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ActionController {

    public void openWebsite(URI uri)  {
        try {
            Desktop.getDesktop().browse(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openWebsite(String website)  {
        URI uri = null;
        try {
            uri = new URI(website);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        openWebsite(uri);
    }
}
