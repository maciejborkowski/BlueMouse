package lex.bluemouse.action;

import java.awt.*;
import java.awt.event.InputEvent;

public class MouseController {
    private Robot robot = null;

    public MouseController() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void move(int x, int y) {
        Point location = MouseInfo.getPointerInfo().getLocation();
        robot.mouseMove(location.x + x, location.y + y);
    }

    public void moveAnimate(int x, int y) {
        Point location = MouseInfo.getPointerInfo().getLocation();
        int sx = location.x;
        int sy = location.y;
        for (int i = 0; i < 30; i++) {
            int mov_x = (x * i) / 30;
            int mov_y = (y * i) / 30;
            robot.mouseMove(sx + mov_x, sy + mov_y);
            if (i + 1 != 30) {
                robot.delay(1);
            }
        }
    }

    public void clickLeft() {
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    public void clickRight() {
        robot.mousePress(InputEvent.BUTTON3_MASK);
        robot.mouseRelease(InputEvent.BUTTON3_MASK);
    }
}
