package lex.bluemouse.transfer;

public interface OrderSender {

    void moveMouse(int x, int y, boolean improved);

    void clickLeftMouse();

    void clickRightMouse();

    void openWebsite(String url);

    void endConnection();

}
