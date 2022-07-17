package com.myspring.design.template;

/**
 * @author jlz
 * @date 2022年07月17日 16:13
 */
public class Client {

    public static void main(String[] args) {
        BasketballGame basketballGame = new BasketballGame();
        basketballGame.paly();

        PingPongGame pingPongGame = new PingPongGame();
        pingPongGame.paly();
    }
}
