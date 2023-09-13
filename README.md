# **Mysql을 활용하여 동시성 이슈를 해결하기 위한 방법 3가지**

### **Pessimistic Lock**
- 실제로 데이터에 Lock을 걸어서 정합성을 맞추는 방법이다. exclusive lock을 걸게되며 다른 트랜잭션에서는 lock이 해제되기 전에 데이터를 가져갈 수 없게된다.
데드락이 걸릴 수 있기에 주의하여 사용하여야 한다.


- **장점** 
1. 충돌이 빈번하게 일어난다면 Optimistic Lock보다 성능이 좋을 수 있음
2. Lock을 통하여 업데이트를 제어하기에 데이터의 정합성이 보장된다.

- **단점**
1. 별도의 Lock을 잡기때문에 성능 감소가 있을 수 있음.

### **Optimistic Lock**
- 실제로 Lock을 이용하지 않고 버전을 이용함으로써 정합성을 맞추는 방법이다. 
- 먼저 데이터를 읽은 후에 update를 수행할 때 현재 내가 읽은 버전이 맞는지 확인하며 업데이트 한다. 내가 읽은 버전에서 수정사항이 생겼을 경우에는 application에서 다시 읽은 후에 작업을 수행해야 한다.

### **Named Lock**
- 이름을 가진 metadata locking이다. 이름을 가진 lock을 획득한 후 해제할 때 까지 다른 세션은 이 lock을 획득할 수 없도록 한다.
   주의할점으로는 transaction이 종료될 때 lcok이 자동으로 해제되지 않는다.
   **별도의 명령어로 해제를 수행**해주거나 선점시간이 끝나야 해제된다.

네임드 락은 주로 분산 락을 구현할 때 사용한다. Pessimistic Lock은 타임아웃을 구현하기 힘들지만 Named Lock은 타임아웃을 손쉽게 구현할 수 있다.
- **단점** 
1. 트랜잭션 종료시에 락 해제, 세션관리를 잘 해주어야 하기때문에 주의해서 사용해야하고, 실제로 사용할 때는 구현방법이 복잡할 수 있음

# **Redis를 활용하여 동시성 이슈 해결하기**
예제에서는 편의를 위하여 JPA의 Native Query 기능을 활용하여 구현하고, 동일한 Datasource를 사용할 것임.
실제로 사용할 때는 DataSource를 분리하여 사용하는것을 추천.  

이유 : 같은 Datasource를 사용하면 커넥션 풀이 부족한 현상이 발생하여 다른 서비스에도 영향을 끼칠 수 있기 때문!

### Lettuce
- setnx 명령어를 활용하여 분산락 구현
- spin lock 방식
- **장점** 구현이 간단하다.
- **단점** spin-lock 방식이기에 redis에 부하를 줄 수 있음. 그래서 락 획득 시도간에 Thread.sleep같은거로 텀을 주어야 함.

### Redisson
- pub-sub 기반으로 Lock 구현 제공
- **장점** 별도의 Lock을 잡지 않으므로 성능상 이점이 있음.
- **단점** 업데이트가 실패하였을 때 재시도 로직을 개발자가 직접 작성해주어야 함.


# 공부할 키워드 키워드 정리
- ExecutorService
- Executors.newFixedThreadPool()
- CountDownLatch(threadCount);
- @Transactional의 propagation 속성
- Lettuce의 setnx 명령어
- spin lock 방식이란?
- Facade 패턴
- 분산 락
- 락 타임아웃 구현