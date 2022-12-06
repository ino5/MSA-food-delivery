# 배달의 민족 : 마이크로서비스 분석/설계 및 구현 실습

참고 템플릿: https://github.com/msa-ez/example-food-delivery



# 서비스 시나리오

기능적 요구사항
1. 고객이 메뉴를 선택하여 주문한다
1. 고객이 선택한 메뉴에 대해 결제한다.
1. 주문이 되면 주문 내역이 입점상점주인에게 주문정보가 전달된다.
1. 상점주는 주문을 수락하거나 거절할 수 있다.
1. 상점주는 요리시작 때와 완료 시점에 시스템에 상태를 입력한다.
1. 고객은 아직 요리가 시작되지 않은 주문은 취소할 수 있다.
1. 요리가 완료되면 고객의 지역 인근의 라이더들에 의해 배송건 조회가 가능하다.
1. 라이더가 해당 요리를 Pick한 후, 앱을 통해 통보한다.
1. 고객이 주문상태를 중간중간 조회한다.
1. 주문상태가 바뀔 때 마다 카톡으로 알림을 보낸다.
1. 고객이 요리를 배달 받으면 배송확인 버튼을 탭하여, 모든 거래가 완료된다.


비기능적 요구사항
1. 트랜잭션
    1. 결제가 되지 않은 주문건은 아예 거래가 성립되지 않아야 한다  Sync 호출 
1. 장애격리
    1. 상점관리 기능이 수행되지 않더라도 주문은 365일 24시간 받을 수 있어야 한다  Async (event-driven), Eventual Consistency
    1. 결제시스템이 과중되면 사용자를 잠시동안 받지 않고 결제를 잠시후에 하도록 유도한다  Circuit breaker, fallback
1. 성능
    1. 고객이 자주 상점관리에서 확인할 수 있는 배달상태를 주문시스템(프론트엔드)에서 확인할 수 있어야 한다  CQRS
    1. 배달상태가 바뀔때마다 카톡 등으로 알림을 줄 수 있어야 한다  Event driven


# 🎈 체크포인트

[분석/설계: 이벤트 스토밍](#-분석설계-이벤트-스토밍)

[새롭게 추가한 기능 2가지](#-새롭게-추가한-기능-2가지)

[체크포인트1. Saga (Pub / Sub)](#-체크포인트1-saga-pub--sub)

[체크포인트2. CQRS](#-체크포인트2-cqrs)

[체크포인트3. Compensation / Correlation](#-체크포인트3-compensation--correlation)

[체크포인트4. Request / Response](#-체크포인트4-request--response)

[체크포인트5. Circuit Breaker](#-체크포인트5-circuit-breaker)

[체크포인트6. Gateway / Ingress](#-체크포인트6-gateway--ingress)





## 🎈 분석/설계: 이벤트 스토밍

![image](https://user-images.githubusercontent.com/70236767/205795328-411fbf9c-0ac4-4f04-8425-96670b4396f9.png)


사진 파일 [링크](./myModeling.png)


## 🎈 새롭게 추가한 기능 2가지

새롭게 추가한 기능으로 쿠폰 기능(쿠폰생성/쿠폰사용)과 주문 옵션 수정 기능이 있다.

### 1. 쿠폰

![image](https://user-images.githubusercontent.com/70236767/205791378-37b8e704-1cac-4f66-ba96-079deb98e552.png)

#### 쿠폰생성

배달이 완료되면 rider 서비스에서 Delivered 이벤트가 발생하면서 publish 한다. 이 때 coupon 서비스에서 createCoupon 정책에 의해 CouponCreated 이벤트가 발생되면서 쿠폰이 생성된다.


#### 쿠폰사용

고객이 use 커맨드를 호출하여 쿠폰을 사용하면 CouponUsed 이벤트가 발생한다. 그리고 Req/Res 방식으로 ordering 서비스의 updateCoupon 커맨드를 호출하여 OrderUpdated 이벤트가 발생되면서 주문에 쿠폰이 적용된다.



### 2. 주문 옵션 수정

![image](https://user-images.githubusercontent.com/70236767/205794609-190d16b0-814f-4339-afb5-048c0fcbf19f.png)

고객이 updateCoupon 커맨드를 호출하여 주문 옵션을 수정하면 OrderUpdated 이벤트가 발생하면서 publish한다. 이 때 updateOrder 정책에 의해 orderUpdated 이벤트가 발생되면서 주문 옵션이 수정된다.



## 🎈 체크포인트1 Saga (Pub / Sub)

### Publish

결제가 이루어졌을 때 Payment의 @PostPersist 어노테이션이 설정되어있는 onPostPersist()에 의해 paid가 publish된다.

ordering 서비스 프로젝트: Payment.java

```java
    /**
        결제가 이루어진 직후 호출 (PostPersist)
     */
    @PostPersist
    public void onPostPersist(){
        // Paid를 publish한다.
        Paid paid = new Paid(this);
        paid.publishAfterCommit();

    }
```

### Subscribe

store 서비스 프로젝트: PolicyHandler.java

```java
    /**
        결제가 되었을 때 (Paid 이벤트) 상태를 변경한다.
     */
    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='Paid'")
    public void wheneverPaid_UpdateStatus(@Payload Paid paid){
        Paid event = paid;
        System.out.println("\n\n##### listener UpdateStatus : " + paid + "\n\n");

        // Sample Logic //
        // FoodCooking의 상태변경하기 (결제 완료 상태)
        FoodCooking.updateStatus(event);
    }
```


## 🎈 체크포인트2 CQRS

CQRS를 통해 주문 상태가 변경되는 이벤트가 발생할 때마다 view의 주문 상태를 변경하도록 한다.


![image](https://user-images.githubusercontent.com/70236767/205803959-2ec262fc-cfe7-40b2-9235-2bd5f4fbc958.png)


![image](https://user-images.githubusercontent.com/70236767/205804108-3a583d28-2411-458a-a05a-93608e4daf38.png)


![image](https://user-images.githubusercontent.com/70236767/205804057-faa0e31b-fe40-41a5-b0e9-8419e432d272.png)

![image](https://user-images.githubusercontent.com/70236767/205804142-c40b2aa8-f5c0-422c-9bee-44a0c9c2f301.png)

![image](https://user-images.githubusercontent.com/70236767/205804302-6ba3641f-9cf9-414c-aed9-50313efec5cb.png)

![image](https://user-images.githubusercontent.com/70236767/205804329-9488a15e-7da1-4f82-8808-dabf9eabde62.png)


```java
@Service
public class MypageViewHandler {

    @Autowired
    private MypageRepository mypageRepository;


    /**
        주문이 들어왔을 때
     */
    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrderPlaced_then_CREATE_1 (@Payload OrderPlaced orderPlaced) {
        try {

            if (!orderPlaced.validate()) return;

            // view 객체 생성
            Mypage mypage = new Mypage();
            // view 객체에 이벤트의 Value 를 set 함
            mypage.setStatus("ordered");
            // view 레파지 토리에 save
            mypageRepository.save(mypage);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
        결제가 되었을 때
     */
    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaid_then_UPDATE_1(@Payload Paid paid) {
        try {
            if (!paid.validate()) return;
                // view 객체 조회
            Optional<Mypage> mypageOptional = mypageRepository.findById(Long.valueOf(paid.getOrderId()));

            if( mypageOptional.isPresent()) {
                 Mypage mypage = mypageOptional.get();
            // view 객체에 이벤트의 eventDirectValue 를 set 함
                mypage.setStatus("paid");    
                // view 레파지 토리에 save
                 mypageRepository.save(mypage);
                }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
        주문이 받아들여졌을 때
     */
    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrderAccepted_then_UPDATE_2(@Payload OrderAccepted orderAccepted) {
        try {
            if (!orderAccepted.validate()) return;
                // view 객체 조회
            Optional<Mypage> mypageOptional = mypageRepository.findById(Long.valueOf(orderAccepted.getOrderId()));

            if( mypageOptional.isPresent()) {
                 Mypage mypage = mypageOptional.get();
            // view 객체에 이벤트의 eventDirectValue 를 set 함
                // view 레파지 토리에 save
                 mypageRepository.save(mypage);
                }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
        주문이 거절되었을 때
     */
    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrderRejected_then_UPDATE_3(@Payload OrderRejected orderRejected) {
        try {
            if (!orderRejected.validate()) return;
                // view 객체 조회

                List<Mypage> mypageList = mypageRepository.findByStatus(orderRejected.getOrderId());
                for(Mypage mypage : mypageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    mypage.setStatus("rejected");
                // view 레파지 토리에 save
                mypageRepository.save(mypage);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
```


<br><br>

## 🎈 체크포인트3 Compensation / Correlation

![image](https://user-images.githubusercontent.com/70236767/205807967-d25ec7f4-ec46-4ba0-8fd9-314aa8bb8e78.png)


주문이 취소될 때 Compensation이 발생한다. Order 클래스에서 @PreRemove 어노테이션이 적용된 onPreRemove 메소드에서 구현한다.

주문취소 이벤트 OrderCanceled를 publish하면서 다른 서비스인 store에서 updateStatus 정책을 통해 상태를 변경할 수 있다.

서로 다른 마이크로서비스 간 데이터 일관성 처리를 위해 사용하는 correlation key에 대해 주문의 아이디인 orderId를 사용한다.


```java
    /**
        주문이 취소될 때 보상 처리
     */
    @PreRemove
    public void onPreRemove(){
        // 주문 취소 publish
        OrderCanceled orderCanceled = new OrderCanceled(this);
        orderCanceled.publishAfterCommit();
    }
```

<br><br>

## 🎈 체크포인트4 Request / Response

![image](https://user-images.githubusercontent.com/70236767/205790898-e4a3aafc-f671-4478-946f-539551faa785.png)


coupon context에서 쿠폰을 사용하면 req/res로 ordering context에 주문을 업데이트하여 쿠폰을 주문에 적용시킨다.



### coupon context

![image](https://user-images.githubusercontent.com/70236767/205777473-681e3afa-2b26-4ae1-88b9-421d78a978cd.png)


Coupon.java

```java
    @PostPersist
    public void onPostPersist(){
        // CouponCreated couponCreated = new CouponCreated(this);
        // couponCreated.publishAfterCommit();

        // 쿠폰을 사용한다면 
        if (isUsed == true) {

            // ordering context에 동기 호출(req / res)

            //Following code causes dependency to external APIs
            // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.            
            mall.external.Order order = new mall.external.Order().;
            
            // 동기 호출 할 때 coupon의 id와 order의 id 담아 보내기
            order.setCouponId(this.getId()); // coupon id
            order.setOrderId(this.getOrderId()); // order id
            
            // mappings goes here
            CouponApplication.applicationContext.getBean(mall.external.OrderService.class)
                .updateCoupon(order);

            // couponUsed publish
            CouponUsed couponUsed = new CouponUsed(this);
            couponUsed.publishAfterCommit();
        }
    }
```

OrderService.java

```java
@FeignClient(name = "ordering", url = "${api.url.ordering}")
public interface OrderService {
    /**
        쿠폰을 사용할 때 주문에 반영하기 위해  ordering context에 호출
     */
    @RequestMapping(method= RequestMethod.PATCH, path="/orders/{id}/updatecoupon)
    public void updateCoupon(@RequestBody Order order);
}


```


### ordering context

OrderController

```java
    /**
        주문에 쿠폰 적용
     */
    @RequestMapping(
        value = "orders/{id}/updatecoupon",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public Order updateCoupon(
        @PathVariable(value = "id") Long id,
        @RequestBody UpdateCouponCommand updateCouponCommand,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /order/updateCoupon  called #####");
        Optional<Order> optionalOrder = orderRepository.findById(id);

        optionalOrder.orElseThrow(() -> new Exception("No Entity Found"));
        Order order = optionalOrder.get();

        // 주문에 쿠폰 적용
        order.updateCoupon(updateCouponCommand);

        orderRepository.save(order);
        return order;
    }
```

<br><br>

## 🎈 체크포인트5 Circuit Breaker

Spring FeignClient + Hystrix를 이용하여 구현한다.

Request/Response 통신 하는 프로젝트의 application.yml 파일에서 다음의 설정을 추가 (본 실습에서 테스트를 위해 default profile에 설정 추가)

```yml
feign:
  hystrix:
    enabled: true
    
hystrix:
  command:
    # 전역설정
    default:
      execution.isolation.thread.timeoutInMilliseconds: 610

```

본 설계에서는 쿠폰을 적용할 때 쿠폰 컨텍스트와 주 컨텍스트 사이의 연결을 RESTful Request/Response 로 연동하여 구현했다. 

요청이 과도할 경우 Circuit Breaker 를 통하여 장애격리한다.

Hystrix 설정
```
execution.isolation.thread.timeoutInMilliseconds: 610
```

요청처리 쓰레드에서 처리시간이 610 밀리가 넘어서기 시작하여 어느정도 유지되면 Circuit Breaker 회로가 닫히도록 (요청을 빠르게 실패처리, 차단) 설정



<br><br>

## 🎈 체크포인트6 Gateway / Ingress

### 로컬 설정

context마다 포트번호를 다르게 설정하였다.

```yml
spring:
  profiles: default
  cloud:
    gateway:
      routes:
        - id: ordering
          uri: http://localhost:8081
          predicates:
            - Path=/orders/**, /payments/**, 
        - id: store
          uri: http://localhost:8082
          predicates:
            - Path=/foodCookings/**, 
        - id: rider
          uri: http://localhost:8083
          predicates:
            - Path=/deliveries/**, 
        - id: customer
          uri: http://localhost:8084
          predicates:
            - Path=, /mypages/**
        - id: coupon
          uri: http://localhost:8085
          predicates:
            - Path=/coupons/**, 
        - id: frontend
          uri: http://localhost:8080
          predicates:
            - Path=/**
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true
```

### 도커 설정

```yml
spring:
  profiles: docker
  cloud:
    gateway:
      routes:
        - id: ordering
          uri: http://ordering:8080
          predicates:
            - Path=/orders/**, /payments/**, 
        - id: store
          uri: http://store:8080
          predicates:
            - Path=/foodCookings/**, 
        - id: rider
          uri: http://rider:8080
          predicates:
            - Path=/deliveries/**, 
        - id: customer
          uri: http://customer:8080
          predicates:
            - Path=, /mypages/**
        - id: coupon
          uri: http://coupon:8080
          predicates:
            - Path=/coupons/**, 
        - id: frontend
          uri: http://frontend:8080
          predicates:
            - Path=/**
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true
```

<br><br>

## Model
www.msaez.io/#/storming/fa783b1b90dfa0eb180753d693a78c47


<br><br>

<br><br>

# 서버 실행 README

## Before Running Services
### Make sure there is a Kafka server running
```
cd kafka
docker-compose up
```
- Check the Kafka messages:
```
cd kafka
docker-compose exec -it kafka /bin/bash
cd /bin
./kafka-console-consumer --bootstrap-server localhost:9092 --topic
```

## Run the backend micro-services
See the README.md files inside the each microservices directory:

- ordering
- store
- rider
- customer


## Run API Gateway (Spring Gateway)
```
cd gateway
mvn spring-boot:run
```

## Test by API
- ordering
```
 http :8088/orders id="id" productId="productId" options="options" addres="addres" customerId="customerId" storeId="storeId" 
 http :8088/payments id="id" orderId="orderId" status="status" 
```
- store
```
 http :8088/foodCookings id="id" status="status" foodId="foodId" orderId="orderId" options="options" storeId="storeId" 
```
- rider
```
 http :8088/deliveries id="id" status="status" orderId="orderId" address="address" 
```
- customer
```
```


## Run the frontend
```
cd frontend
npm i
npm run serve
```

## Test by UI
Open a browser to localhost:8088

## Required Utilities

- httpie (alternative for curl / POSTMAN) and network utils
```
sudo apt-get update
sudo apt-get install net-tools
sudo apt install iputils-ping
pip install httpie
```

- kubernetes utilities (kubectl)
```
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
```

- aws cli (aws)
```
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```

- eksctl 
```
curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp
sudo mv /tmp/eksctl /usr/local/bin
```

