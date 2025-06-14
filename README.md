[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/YCWZwwXS)

# Evaluation

## Kubernetes (K8s) [V]
With Kubernetes different containers can be hosted and managed individually.
Kubernetes can be run locally or on a server/cloud provider.
Additionally, to the basic hosting service, Kubernetes features a number of 
additional features that can be used to ensure the availability of the services.

https://kubernetes.io/

### Minikube [Y]

[Minikube](https://minikube.sigs.k8s.io/docs/) is a nice solution to set up a lightweight local Kubernetes cluster on a dev machine to simulate the production environment. It will be used for experimentation with deployments, configurations and operators and see them interact with each other in a production-like environment. The setup should be as close to a real production setup as possible and make use of the advantages of cloud infrastructure.

### Load Balancing [V]
For Load-Balancing to be possible first it is necessary to have the amount of 
resources available to run multiple instance. 
Load-balancing can (but must not) be combined with automatic scaling.
In Kubernetes one can choose between different load-balancing methods, 
that work more or less automatically.

In order for load-balancing to work properly in our case there are a few things to consider:
- Authentication
  - How is the user-session preserved over multiple requests, so that the user does not need to log in again, 
    if they get sent to a different  server.
- Database
  - Would we use one or multiple databases to store the sensor information?
  - How would the databases be synced? -> eventual consistency?
  - If we used only one database, would the load-balancing even have an effect, 
    since the computations themselves are not expensive.

https://www.apptio.com/topics/kubernetes/best-practices/load-balancer/?src=kc-com

### Resilience [V]
In Kubernetes one can (relatively) easily setup different methods to improve the resilience of an application. 
These methods are exemplary:


- Self-healing
  - Restarting containers that crash
  - Depending on how the software works internally this can help or the issue can just reoccur
  - Automatically also attached storage
  - Requires health-checks on the application
    - liveness probe (nodes can be restarted when this fails)
    - readiness probe
    - startup probe
- Horizontal resource scaling
  - The application can be scaled up either automatically or manually
  - Of course requires more available resources to be accessed (over a cloud provider)

https://blog.devgenius.io/building-resilient-applications-in-kubernetes-a-hands-on-guide-9f5fffc14b9b

### Blue-green deployment [Y]

Blue-green deployment is a stratregy to reduce downtime and risk by running two sseparate production environments in parallel, the "blue" (current) version and the "green" (new) version. During deployment, the green version gets fully deployed and possibly tested before traffic is switched over from the blue enrionment.

This should help to reduce downtime for the Java Application, since the webserver takes some time to start and set up until it is ready to receive traffic. Users should not notice any interruptions during deployment.

It should also allow for safer rollbacks, since the traffic only gets routed to the new version, when the startup is complete and health checks have been performed.

### Database in Kubernetes [Y]

The Postgres database should run in the Kubernetes cluster alongside the application sservices to make use of the declarative deployment process and scaling options provided by the cloud infrastructure. However, this introduces some challenges related to persistence and availablity.

The database will be deployed as a service in the cluster and use a **PersistentVolumeClaim** to ensure that the data is not lost when the pod is restarted. This provides simple data persistence that is suitable for our test deployment and development use case.

All in all, the data base service will most likely only use a single node. The data will be stored via the PVC that is managed by Kubernetes. No backup or restore mechanism is planned for this setup, although it would be a possible extension on the project and necessary for a real-world production environment.

Another important factor is, that the database should only be exposed inside the cluster and serve the application, but never be accessible from the outside.

### Keycloak in Kubernetes vs. Online Keycloak-Provider [V]
#### Hosting Keycloak in Kubernetes
The following approach creates a fresh Keycloak instance in Kubernetes. 
This does not yet handle a realm-import at creation time.
https://www.keycloak.org/getting-started/getting-started-kube

- Automatically importing realm might prove tricky, since the Keycloak Operator does not support this out of the box.
  - It would be necessary to either manually import or create the realm from scratch
- Can be tricky to handle the SSL certificates to work with RabbitMQ

#### Online Keycloak-Provider
https://www.cloud-iam.com/

The usage of an online Keyclaok provider would be a good alternative, since
- It requires only a one-time setup of the realm in a secluded application
- No handling of Keycloak in Kubernetes is necessary
- In the free version still no SSL certificate are available
  - Might prove even trickier to install one there, since we probably won't have access to the hosted keycloak instance directly.

### RabbitMQ in Kubernetes [V]
To deploy RabbitMQ in Kubernetes the [**RabbitMQ Operator**](https://www.rabbitmq.com/kubernetes/operator/operator-overview) should be used.
It is important to note RabbitMQ provides two different operators:
- RabbitMQ Cluster Kubernetes Operator
- RabbitMQ Messaging Topology Operator

For the **RabbitMQ Cluster Kubernetes Operator** to work properly [no custom credentials](https://www.rabbitmq.com/kubernetes/operator/operator-overview#top-op-limitations)
should be used in **RabbitMQ Messaging Topology Operator**, since it relies on the default credentials.

In order to successfully run [RabbitMQ](https://www.rabbitmq.com/kubernetes/operator/tls-topology-operator) TLS needs to be setup.
And one needs to pass the CA (Certificate Authority) to the RabbitMQ Operator.

If problems persist with the certificate setup, an alternative is https://mosquitto.org/.
The Mosquitto-Broker is able to also handle non-secure connections, which would allow for a simpler setup.

## OAuth2 (over Kewycloak) with RabbitMQ [Y]

In previous iterations of this project, there were some issues setting up the OAuth2 mechanism with Keycloak and RabbitMQ, since it is apparently not possible to configure RabbitMQ without https enabled. Even so, it would not accept a self-signed certificate, which did not allow for the sensor or the application to authenticate using the token provided by Keycloak.

In this iteration, it will again be attempted to make this approach work, in order to manage all authentication and authorization through Keycloak, since the goal is a centralized authentication and authorization provider for a consistent token-based access control across all services. If RabbitMQ can still not be configured correctly, another option like Mosquitto may be tried as a Broker between the sensor messages and the application.

The currently functional apprach using the internal backend of RabbitMQ for authentication and authorization should only be used as a fallback method.

If problems persi

## CI/CD-Pipeline (GitHub Actions) [Y]

Continous Integration and Deployment is an essential component to effectively and reliably publish an application, especially to a container platform. By using these practices, a clean deployment can be guaranteed.

<div style="display: flex; justify-content: space-between;">

<div style="flex: 2; min-width: 70%">

To ensure that a working version of the application can always be delivered, the new code should be checked before it can be committed to the main/master branch. This is achieved by implementing a CI pipeline that runs on pull requests on the main/master branch. In an application that is actually deployed to a production system, direct push to main/master should be disabled, so all code that is running on production is always evaluated against the quality gates that have been set. However, enforcing such standards are limited by the features that are available in the Free Github Repository.

By following a pull request workflow, it is also possible to work on multiple independent feature branches at once, so feature development can happen more smoothly.

</div>

<div style="flex: 1; min-width: 15%; justify-items: end;">

<img src="images/feature-integration.png" alt="Feature Integration" style="width: 90%; height: auto; max-height: 100%">

</div>

</div>

Every time code is pushed to the main/master branch, a build should be performed and the built service or application should immediately be deployed to the test system with a new version number. Version numbers can follow various patters, such as the semver style with major, minor and patch versions. Other styles using the date and time of the build are also viable, especially if the development does not culminate in regular milestones. By doing this, the running application can also be checked on a production-like environment to further ensure that the build will run in the production environment.

This process may be repeated several times, until a stable and complete version is released to the production system.

![](images/cicd.png)

Since the repository is hosted on Github, [Github Actions](https://github.com/features/actions) will be used to implement a CI/CD Pipeline. Since the application is based on Spring and Java/Kotlin, preconfigured actions from the Github catalogue can be used to perform common tasks, like preparing the JDK and building the application using Gradle. This is adventageous, since Github Actions can also be performed locally using the `act` toolset.

Github Actions will be used to perform code checks, build the app jar, build an application container, push the container to an image registry and deploy the image to the target system.

# Conclusio
- Minikube will be used as a simulated Kubernetes environment
  - Enables testing of deployment manifests and service configurations
  - Blue-green Deployment for reducing downtime during deployment
- Simple Resilience measures will be used
  - Automatic restarting of failing containers
- RabbitMQ will be hosted in Kubernetes
  - If problems arise with RabbitMQ. It can be replaced with Mosquitto.
- Keycloak will be used over the remote provider
  - A certificate will be setup by routing the service over an nginx reverse proxy
- Postgres Database will run inside Kubernetes usng a PersistentVolumeClaim
- A CI/CD Pipeline using GithHub Actions will be implemented
  - Ensures reliable and automated builds and deployments
  - Enables rapid development workflows