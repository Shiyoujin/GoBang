package com.example.fivegame.config;

import org.springframework.stereotype.Component;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

/**
 * @author white matter
 * 由于HTTP协议与websocket协议的不同，
 * 导致没法直接从websocket中获取协议，之前写了获取HttpSession的代码，但是如果真的放出去执行，
 * 那么会报空指值异常，因为这个HttpSession并没有设置进去。
 * 我们来设置HttpSession。这时候我们需要写一个监听器
 */
@Component
public class RequestListener implements ServletRequestListener {

    @Override
    public void requestInitialized(ServletRequestEvent sre)  {
        //将所有request请求都携带上httpSession
        ((HttpServletRequest) sre.getServletRequest()).getSession();

    }
    public RequestListener() {
    }

    @Override
    public void requestDestroyed(ServletRequestEvent arg0)  {
    }
}
