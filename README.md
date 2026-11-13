# Kızıl Asistan — Android prototipi

Bu klasör, Android Studio ile açılabilen başlangıç projesidir.

## Bu sürümde çalışanlar
- Türkçe (`tr-TR`) Android konuşma tanıma
- Android Text-to-Speech ile Türkçe sesli çıktı (cihazda Türkçe ses paketi bulunmasına bağlı)
- Tam ekran koyu arayüz ve basit avatar yer tutucusu
- Konuşma durumuna göre hafif animasyon

## Henüz eklenmeyenler
- OpenAI API bağlantısı: şu an yanıt metni prototip amaçlı sabittir.
- Gerçek portre görseli ve gerçek dudak senkronizasyonu: emoji/animasyon yer tutucudur.
- Arka planda sürekli dinleme: gizlilik ve pil tüketimi nedeniyle bu sürümde yoktur; kullanıcı mikrofon düğmesine basar.

## Android Studio ile çalıştırma
1. Android Studio'da bu klasörü açın.
2. Gradle senkronizasyonunu tamamlayın (JDK 17 veya uyumlu Android Studio JDK kullanın).
3. Android 8.0+ cihazı USB hata ayıklama ile bağlayın veya emülatör başlatın.
4. Run düğmesine basın.
5. İlk kullanımda mikrofon iznini verin.

## Sonraki geliştirme adımları
1. Kullanıcının onayıyla portre görselini `app/src/main/res/drawable/` içine eklemek.
2. Ağız hareketlerini gerçek ses seviyesine/ses zamanlamasına bağlamak.
3. API anahtarını uygulamaya koymadan, kimlik doğrulamalı küçük bir sunucu üzerinden OpenAI yanıt servisini bağlamak.
4. İstenirse gerçek zamanlı ses için güncel OpenAI Realtime API entegrasyonunu eklemek.

**Güvenlik:** OpenAI API anahtarını APK içine veya kaynak koda koymayın. Sunucu tarafında saklayın.
