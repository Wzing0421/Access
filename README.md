Introduction:
这个是我自己建议封装好的java k8s客户端.
请注意连接的时候需要配好环境, 也就是配置好 String httpApi = "http://162.105.85.63:8008";
需要在/etc/kubernetes/manifests/kube-apiserver.yaml中加上：
- --enable-swagger-ui=true
- --insecure-bind-address=0.0.0.0
- --insecure-port=8008
然后等待一会就可以访问了,这样可以避免配置令牌token那些比较烦人的信息.
Good luck to everyone!