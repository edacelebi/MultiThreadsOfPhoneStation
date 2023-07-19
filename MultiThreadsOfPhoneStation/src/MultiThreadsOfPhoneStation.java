import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class MultiThreadsOfPhoneStation   implements Runnable {


    static Semaphore Operator = new Semaphore(2);  // operatör erişim kontrolü
    static Semaphore Hat = new Semaphore(1); // Hat erişim kontrolü
    static int bekleyen_operator = 0; // Operatör kuyruğunu tut
    static int bekleyen_hat = 0; // Hat kuyruğunu tut
    static int sayac = 0; // Sayaç
    
    public MultiThreadsOfPhoneStation(){
        new Thread(new Runnable() { // Konsolu diğer iş parçacıklarından güncelle
            @Override
            public void run() {
                do {
                    durumYazdır();
                    try {
                        Thread.sleep(50); // Her 50 ms'de bir güncelleme
                    }
                    catch (InterruptedException e){
                        throw new RuntimeException(e);
                    }
                }
                while (sayac < 20); // 20 kişi konuşana kadar işlem devam edecek.
                durumYazdır();
                System.out.println("Herkes konuştu.");
                
            }
        }
        ).start();
    }
    @Override
    public void run() {
        bekleyen_operator++;  // Operatör bekleme kuyruğuna thread ekleyin
        try {
            getOperator();
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }
    private void getOperator() throws InterruptedException{
        if(Operator.tryAcquire()){
            Thread.sleep((long)(Math.random() * 1000)); // iletişim operatörü
            bekleyen_operator--;
            bekleyen_hat++;
            getHat();
        }
        else{
            Thread.sleep((long) (Math.random() * 1000)); // iletişim operatörü
            getOperator();
        }
    }
    private synchronized void getHat() throws InterruptedException{
        if(!Hat.tryAcquire()){
            wait();
        }
        Thread.sleep((long) (Math.random() * 1000)); // Rastgele konuşma süresi
        sayac++; // sayaç güncelleme
        bekleyen_hat--;
        Hat.release(); // Yayın satırı semaforu
        Operator.release(); // Operator satırı semaforu
        notify(); 
    }
    private void durumYazdır(){ // Geçerli durumu yazdır
        System.out.println("Hat: " + Hat.availablePermits());
        System.out.println("Operatorler: " + Operator.availablePermits());
        System.out.println("Bekleyen_Hat: " + bekleyen_hat);
        System.out.println("Bekleyen_Operator: " + bekleyen_operator);
    }
}