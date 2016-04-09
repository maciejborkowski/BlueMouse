package lex.bluemouse.action;

import java.awt.*;
import java.awt.event.InputEvent;

public class MouseController {
    private Robot robot = null;

    public MouseController()  {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
    public void move(int x, int y) {
        robot.mouseMove(x, y);
    }

    public void click() {
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

}
