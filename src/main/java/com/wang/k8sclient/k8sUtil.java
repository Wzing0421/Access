package com.wang.k8sclient;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.ClientBuilder;
import org.springframework.stereotype.Component;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;

import java.io.IOException;
import java.util.*;

@Component
public class k8sUtil {

    public static ApiClient client = null;

    public static final String httpApi = "http://162.105.85.63:8008";

    public static ApiClient getApiClient() throws IOException {
        if (client == null) {
            synchronized (k8sUtil.class) {
                client = new ClientBuilder().setBasePath(httpApi).setVerifyingSsl(false).build();
                Configuration.setDefaultApiClient(client);
            }
        }
        return client;
    }

    public void getAllPodList() throws Exception{

        if(client == null) client = getApiClient();
        CoreV1Api api = new CoreV1Api();
        V1PodList list = api.listPodForAllNamespaces(null,null,null,null,null,null,null,null,null);
        for(V1Pod item : list.getItems()){
            System.out.println(item);
        }
    }
    public void getNamespace() throws IOException, ApiException{
        if(client == null) client = getApiClient();
        CoreV1Api api = new CoreV1Api();
        V1NamespaceList list = api.listNamespace(null,null,null,null,null,null,null,null,null);
        for(V1Namespace l : list.getItems()){
            System.out.println(l);
        }
    }

    //这个现在还有问题
    public Object createNamespace(String name) throws IOException, ApiException{
        if(client == null) client = getApiClient();
        CoreV1Api api = new CoreV1Api();
        AppsV1Api appsV1Api = new AppsV1Api();
        V1Namespace body = new V1Namespace();
        body.setApiVersion("v1");
        body.setKind("Namespace");

        V1ObjectMeta meta = new V1ObjectMeta();
        //meta.setNamespace(name);
        meta.setName(name);
        meta.setNamespace(name);
        body.setMetadata(meta);
        //        body.setApiVersion("apps/v1");
//        body.setKind("Deployment");
        /*V1Deployment body=new V1Deployment();
        body.setApiVersion("apps/v1");
        body.setKind("Deployment");
        V1ObjectMeta meta=new V1ObjectMeta();
        meta.setNamespace(name);
        meta.setName(name);
        body.setMetadata(meta);
        V1DeploymentSpec spec=new V1DeploymentSpec();
        spec.setReplicas(1);
        body.setSpec(spec);*/
        try {
            //appsV1Api.createNamespacedDeployment(name, body, true, null, null);
            V1Namespace result =api.createNamespace(body,"true", null, null);
            System.out.println(result);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return body;
    }


    //创建一个pod
    public Object createPod() throws IOException, ApiException{
        //https://github.com/kubernetes-client/java/issues/274 这个是我随便从网上找的还没改动，请明天进行修改
        if(client == null) client = getApiClient();
        CoreV1Api api = new CoreV1Api();

        V1Pod pod = new V1Pod();
        pod.setApiVersion("v1");
        pod.setKind("Pod");

        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName("test-pod");
        metadata.setLabels(new HashMap<>());
        metadata.getLabels().put("k8s-app", "someapp");
        pod.setMetadata(metadata);

        V1PodSpec spec = new V1PodSpec();
        spec.setContainers(new ArrayList<>());
        V1Container container = new V1Container();

        //设置cpu和内存
        container.setResources(new V1ResourceRequirements());
        container.getResources().setRequests(new HashMap<>());
        container.getResources().getRequests().put("memory", new Quantity("64M"));
        container.getResources().getRequests().put("cpu", new Quantity("250m"));

        container.setName("myudpserver-containers");
        container.setImage("myudpserver:v1");
        V1ContainerPort port = new V1ContainerPort();
        port.containerPort(20100);
        port.setProtocol("UDP");
        container.setPorts(Arrays.asList(port));
        container.setImagePullPolicy("IfNotPresent");

        /*
        //这部分是健康检测的探针，先不用
        V1Probe readinessProbe = new V1Probe();
        V1HTTPGetAction readinessHttpGet = new V1HTTPGetAction();
        readinessHttpGet.setPath("/alive");
        readinessHttpGet.setPort(new IntOrString(8080));
        readinessProbe.setHttpGet(readinessHttpGet);
        container.setReadinessProbe(readinessProbe);*/

        spec.getContainers().add(container);
        pod.setSpec(spec);
        try {
            V1Pod v1pod = api.createNamespacedPod("default", pod, "true", null, null);
            System.out.println(v1pod);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    public Object createService() throws IOException, ApiException{
        if(client == null) client = getApiClient();
        CoreV1Api api = new CoreV1Api();
        V1Service svc = new V1Service();

        try {
            V1Service v1Service = api.createNamespacedService("default", svc, "true", null, null);
            System.out.println(v1Service);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;

    }*/

    //删除一个pod
    public void deletePod() throws IOException, ApiException{
        if(client == null) client = getApiClient();
        CoreV1Api api = new CoreV1Api();
        api.deleteNamespacedPod("test-pod", "default", "true", null, 1, null, null,null);
    }

    public V1Deployment createDeployment(String nameSpace, String deployedName) throws ApiException, IOException {
        if(client == null) client = getApiClient();
        AppsV1Api api = new AppsV1Api();

        //set kind and api version
        V1Deployment deployment1 = new V1Deployment();
        deployment1.setKind("Deployment");
        deployment1.setApiVersion("apps/v1");

        //set metadata
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(deployedName);
        metadata.setNamespace(nameSpace);
        metadata.setLabels(new HashMap<>());
        metadata.getLabels().put("k8s-app", "myudpserver");
        deployment1.setMetadata(metadata);

        // spec
        V1DeploymentSpec v1DeploymentSpec = new V1DeploymentSpec();
        v1DeploymentSpec.setReplicas(3);
        v1DeploymentSpec.setRevisionHistoryLimit(10);
        v1DeploymentSpec.setSelector(new V1LabelSelector());
        v1DeploymentSpec.getSelector().setMatchLabels(new HashMap<>());
        v1DeploymentSpec.getSelector().getMatchLabels().put("k8s-app", "myudpserver");


        V1PodTemplateSpec v1PodTemplateSpec = new V1PodTemplateSpec();
        //set pod metadata
        V1ObjectMeta podMetadata = new V1ObjectMeta();
        podMetadata.setLabels(new HashMap<>());
        podMetadata.getLabels().put("k8s-app", "myudpserver");
        v1PodTemplateSpec.setMetadata(podMetadata);
        //set podspec
        V1PodSpec v1podspec = new V1PodSpec();
        V1Container container = new V1Container();

        //设置cpu和内存
        container.setResources(new V1ResourceRequirements());
        container.getResources().setRequests(new HashMap<>());
        container.getResources().getRequests().put("memory", new Quantity("64M"));
        container.getResources().getRequests().put("cpu", new Quantity("250m"));

        container.setName("myudpserver-containers");
        container.setImage("myudpserver:v1");
        V1ContainerPort port = new V1ContainerPort();
        port.containerPort(20100);
        port.setProtocol("UDP");
        container.setPorts(Arrays.asList(port));
        container.setImagePullPolicy("IfNotPresent");
        v1podspec.getContainers().add(container);
        v1PodTemplateSpec.setSpec(v1podspec);

        v1DeploymentSpec.setTemplate(v1PodTemplateSpec);

        deployment1.setSpec(v1DeploymentSpec);

        try {
            V1Deployment result = api.createNamespacedDeployment(nameSpace, deployment1, "true", null, null);
            System.out.println(result);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;

    }

    public void deleteDeployment(String nameSpace, String deployedName) throws IOException, ApiException{
        if(client == null) client = getApiClient();
        AppsV1Api api = new AppsV1Api();
        api.deleteNamespacedDeployment(deployedName, nameSpace, "true", null, 1, null, null, null);
    }

    public V1Service createService(String nameSpace, String serviceName) throws IOException, ApiException{
        if(client == null) client = getApiClient();
        CoreV1Api api = new CoreV1Api();

        V1Service v1Service = new V1Service();
        v1Service.setKind("Service");
        v1Service.setApiVersion("v1");

        //set metadata
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(serviceName);
        metadata.setNamespace(nameSpace);
        metadata.setLabels(new HashMap<>());
        metadata.getLabels().put("k8s-app", "myudpserver");
        v1Service.setMetadata(metadata);

        V1ServiceSpec v1ServiceSpec = new V1ServiceSpec();
        //set ports
        List<V1ServicePort> ports = new ArrayList<>();
        V1ServicePort v1ServicePort = new V1ServicePort();
        v1ServicePort.setPort(20100);
        v1ServicePort.setTargetPort(new IntOrString(20100));
        v1ServicePort.setNodePort(31000);
        v1ServicePort.protocol("UDP");
        ports.add(v1ServicePort);
        v1ServiceSpec.setPorts(ports);

        v1ServiceSpec.setSelector(new HashMap<>());
        v1ServiceSpec.getSelector().put("k8s-app", "myudpserver");
        v1ServiceSpec.setType("NodePort");
        v1Service.setSpec(v1ServiceSpec);

        try{
            V1Service result = api.createNamespacedService(nameSpace, v1Service, "true", null, null);
            System.out.println(result);
        }catch (ApiException e){
            e.printStackTrace();
        }
        return null;
    }

    public void deleteService(String nameSpace, String serviceName) throws IOException, ApiException{
        if(client == null) client = getApiClient();
        CoreV1Api api = new CoreV1Api();
        api.deleteNamespacedService(serviceName, nameSpace, "true", null, 1, null, null, null);
    }
}
