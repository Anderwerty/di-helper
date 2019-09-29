package org.di.provider;

import org.di.provider.service.Service;

public class Main  extends Runner{
    public static void main(String[] args) throws Exception {

        Main main = new Main();
        main.run();
        System.out.println(main.size());

        Service service =(Service) main.getBean("Service");
        service.printInformation();
    }
}
