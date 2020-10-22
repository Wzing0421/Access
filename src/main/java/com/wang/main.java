package com.wang;
import com.wang.k8sclient.k8sUtil;

public class main {
    public static void main(String[] args) throws Exception {
        k8sUtil k8sutil = new k8sUtil();
        k8sutil.getNamespace();
        //k8sutil.createNamespace("testNamespace");
        //k8sutil.createPod();
        //k8sutil.createDeployment("default", "myudpserver");
        //k8sutil.deleteDeployment("default", "myudpserver");
        //k8sutil.createService("default", "myudpserver");
        k8sutil.deleteService("default", "myudpserver");
    }
}
