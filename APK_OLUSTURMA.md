# Telefonda, bulut üzerinden APK oluşturma

Bu projeye `.github/workflows/android-apk.yml` eklendi. GitHub Actions, Android derlemesini GitHub sunucularında yapar; bilgisayar veya Android Studio gerekmez.

1. GitHub hesabı açın/giriş yapın: https://github.com
2. Sağ üstte `+` > `New repository` seçin. Depo adı `kizil-asistan`, görünürlük `Private` olsun.
3. `Add file` > `Upload files` ile ZIP'i önce telefonda çıkarıp içindeki proje dosyalarını (özellikle `.github` klasörü dahil) depoya yükleyin. ZIP dosyasının kendisini değil, içeriğini yükleyin.
4. `Commit changes` düğmesine basın.
5. Depoda `Actions` sekmesine girin. `Build Kizil Asistan APK` iş akışını seçip `Run workflow` düğmesine basın.
6. İşlem tamamlanınca ilgili çalışmayı açın. `Artifacts` bölümünden `Kizil-Asistan-debug-APK` dosyasını indirin.
7. İnen ZIP'i açın; `app-debug.apk` dosyasını seçip Android'in kurulum uyarılarını izleyin.

Notlar:
- İlk derleme birkaç dakika sürebilir.
- Bu yalnızca prototip debug APK'sıdır; mağazaya dağıtım için imzalı release APK değildir.
- Bu prototipte OpenAI bağlantısı ve gerçek portre/dudak senkronizasyonu henüz yoktur. Mikrofonla Android konuşma tanıma ve cihazın Türkçe metin-ses motoru bulunur.
- Mikrofon yalnızca kullanıcı düğmeye bastığında kullanılır.
