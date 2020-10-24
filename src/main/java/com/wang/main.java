package com.wang;
import com.wang.k8sclient.k8sUtil;

public class main {
    public static void main(String[] args) throws Exception {
        k8sUtil k8sutil = new k8sUtil();
        //k8sutil.getNamespace();
        //k8sutil.createNamespace("testNamespace");
        //k8sutil.createPod("default", "test-resource-pod2");
        //k8sutil.deletePod("default", "test-resource-pod2");
        //k8sutil.getPodStatus("someapp");
        k8sutil.watchPod();
        //k8sutil.createDeployment("default", "testudpserver");
        //k8sutil.deleteDeployment("default", "testudpserver");
        //k8sutil.createService("default", "myudpserver");
        //k8sutil.deleteService("default", "myudpserver");
    }
}
