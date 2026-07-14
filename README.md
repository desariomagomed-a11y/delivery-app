# Delivery App - Full Integration Update

Полная интеграция всех компонентов: **Firebase**, **2ГИС**, **Платежи**, **Локация в реальном времени**, **Рейтинги**, **Уведомления**.

---

## 🔥 Firebase Firestore интеграция

### FirebaseService
- ✅ Сохранение и получение рейтингов
- ✅ Управление заказами
- ✅ Сохранение данных пользователей
- ✅ Обновление статуса доставки
- ✅ Получение курьеров рядом

```kotlin
// Пример использования
val firebaseService = FirebaseService(firestore)

// Сохранить рейтинг
val rating = Rating(score = 4.5f, comment = "Great!")
firebaseService.submitRating(rating)

// Получить доступные заказы
val orders = firebaseService.getAvailableOrders()
```

---

## 💳 Система платежей (Stripe/PayPal)

### PaymentRepository
- ✅ Обработка платежей через Stripe
- ✅ Расчет цены доставки
- ✅ История платежей
- ✅ Возврат денег (Refund)
- ✅ Система чаевых (Tips)

```kotlin
// Расчет стоимости
val estimate = paymentRepository.calculatePriceEstimate(
    distance = 2.5, // км
    estimatedTime = 15 // минут
)
// Результат: $2.50 (базовая) + $3.75 (расстояние) + $3.75 (время) = $10.00

// Процесс платежа
paymentRepository.processPayment(
    orderId = "order_123",
    userId = "user_456",
    amount = 10.0,
    token = "stripe_token"
)
```

### PaymentScreen
- Выбор способа оплаты (Карта, PayPal, Google Pay, Apple Pay)
- Введение данных карты
- Система чаевых (2%, 5%, 10%, Custom)
- Подтверждение платежа
- Экран успеха

---

## 📍 Real-time GPS отслеживание

### LocationTrackingRepository
- ✅ Получение обновлений GPS каждые 5 секунд
- ✅ Сохранение контрольных точек в Firestore
- ✅ Расчет расстояния между точками
- ✅ Калькуляция скорости доставки

```kotlin
// Получить обновления локации
val locationUpdates = locationTrackingRepository.getLocationUpdates()

// Сохранить контрольную точку
locationTrackingRepository.saveTrackingPoint(
    orderId = "order_123",
    latitude = 40.7128,
    longitude = -74.0060,
    speed = 32.5f
)

// Получить маршрут доставки
val deliverySummary = locationTrackingRepository.getDeliverySummary(orderId)
```

### LiveTrackingScreen
- 📍 Визуализация маршрута на 2ГИС карте
- 👤 Информация о курьере
- ⏱️ ETA (Estimated Time of Arrival)
- 🚗 Текущая скорость
- 📊 История отслеживания
- 📞 Кнопка вызова курьера

---

## 🔔 Push Notifications (FCM)

### NotificationRepository
- ✅ Сохранение FCM токена
- ✅ Отправка уведомлений
- ✅ Подписка на обновления заказа
- ✅ Отметить как прочитано

### Типы уведомлений
```
- ORDER_CREATED         - Заказ создан
- ORDER_ASSIGNED        - Курьер назначен
- ORDER_PICKED_UP       - Заказ получен
- ORDER_IN_TRANSIT      - В пути
- ORDER_DELIVERED       - Доставлено
- RATING_RECEIVED       - Получена оценка
- PAYMENT_COMPLETED     - Платёж прошёл
- PAYMENT_FAILED        - Ошибка платежа
- COURIER_ARRIVING      - Курьер приезжает
- COURIER_ARRIVED       - Курьер прибыл
```

### NotificationsScreen
- Список всех уведомлений
- Фильтрация по прочитанности
- Удаление уведомлений
- Иконки по типам
- Форматированное время

---

## ⭐ Система рейтинга

### RatingRepository
- ✅ Сохранение оценок
- ✅ Получение рейтинга пользователя
- ✅ Автоматический расчёт среднего рейтинга
- ✅ Статистика курьера

---

## 🚀 ViewModel - Управление состоянием

```kotlin
class DeliveryViewModel(
    paymentRepository: PaymentRepository,
    ratingRepository: RatingRepository,
    locationTrackingRepository: LocationTrackingRepository,
    notificationRepository: NotificationRepository
)

// Состояния:
sealed class PaymentState {
    object Idle
    object Processing
    data class Success(val payment: Payment)
    data class Error(val message: String)
    data class PriceEstimate(val estimate: PriceEstimate)
}

sealed class RatingState {
    object Idle
    object Submitting
    object Success
    data class Error(val message: String)
}
```

---

## 📁 Структура проекта

```
app/src/main/kotlin/com/deliveryapp/
├── data/
│   ├── models/
│   │   ├── Rating.kt              ⭐ Рейтинги
│   │   ├── Payment.kt             💳 Платежи
│   │   ├── Notification.kt        🔔 Уведомления
│   │   ├── Location2GIS.kt        📍 2ГИС
│   │   └── Order.kt
│   │
│   ├── remote/
│   │   ├── FirebaseService.kt     🔥 Firebase
│   │   ├── PaymentApi.kt          💳 Stripe/PayPal
│   │   ├── Maps2GISApi.kt         📍 2ГИС
│   │   └── DeliveryFirebaseMessagingService.kt 🔔 FCM
│   │
│   └── repository/
│       ├── PaymentRepository.kt    💳
│       ├── RatingRepository.kt     ⭐
│       ├── NotificationRepository.kt 🔔
│       ├── LocationTrackingRepository.kt 📍
│       └── Maps2GISRepository.kt   📍
│
├── ui/screens/
│   ├── PaymentScreen.kt            💳 Экран оплаты
│   ├── NotificationsScreen.kt      🔔 Уведомления
│   ├── LiveTrackingScreen.kt       📍 Отслеживание
│   ├── Map2GISScreen.kt            📍 Карта
│   └── ...
│
├── services/
│   └── DeliveryFirebaseMessagingService.kt
│
└── viewmodel/
    └── DeliveryViewModel.kt         🎯 State management
```

---

## ⚙️ Конфигурация

### 1. Firebase Setup
```gradle
// build.gradle.kts
implementation(platform("com.google.firebase:firebase-bom:32.5.0"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-messaging-ktx")
```

### 2. Stripe Integration
```gradle
// Добавить в PaymentApi
private val API_KEY = "sk_test_YOUR_STRIPE_KEY"
```

### 3. 2ГИС API
```gradle
// Добавить в Maps2GISRepository
private val API_KEY = "YOUR_2GIS_API_KEY"
```

### 4. Google Location Services
```gradle
implementation("com.google.android.gms:play-services-location:21.0.1")
```

---

## 🔄 Полный цикл заказа с интеграциями

```
1. Клиент создает заказ
   ↓
2. Система рассчитывает цену (PaymentRepository)
   ↓
3. Клиент выбирает способ оплаты (PaymentScreen)
   ↓
4. Платеж обрабатывается (Stripe API)
   ↓
5. Уведомление отправляется курьеру (FCM)
   ↓
6. Курьер принимает заказ
   ↓
7. Начинается отслеживание GPS (LocationTrackingRepository)
   ↓
8. Контрольные точки сохраняются в Firestore
   ↓
9. Клиент видит курьера на карте (LiveTrackingScreen)
   ↓
10. Доставка завершена
    ↓
11. Клиент оценивает курьера (RatingScreen)
    ↓
12. Оценка сохраняется (FirebaseService)
    ↓
13. Уведомление о новой оценке отправляется курьеру
    ↓
14. Рейтинг курьера обновляется (RatingRepository)
```

---

## 📊 Интеграция моделей данных

```
Order
  ├─ customerId → User
  ├─ courierId → Courier
  ├─ Payment
  │   ├─ method: PaymentMethod
  │   ├─ status: PaymentStatus
  │   └─ transaction: Transaction
  │
  ├─ Tracking
  │   ├─ currentLocation: (lat, lon)
  │   ├─ trackingPoints: List<DeliveryTrackingPoint>
  │   └─ RouteInfo (from 2GIS)
  │
  ├─ Rating (после доставки)
  │   ├─ score: Float
  │   └─ comment: String
  │
  └─ Notifications
      ├─ OrderUpdate
      ├─ PaymentStatus
      └─ TrackingUpdate
```

---

## 🎯 Использование ViewModel

```kotlin
// В экране
val viewModel: DeliveryViewModel = viewModel()
val paymentState = viewModel.paymentState.collectAsState()
val notifications = viewModel.notifications.collectAsState()

// Обработка платежа
Button(onClick = {
    viewModel.processPayment(orderId, userId, amount, token)
}) {
    Text("Pay Now")
}

// Отправка оценки
Button(onClick = {
    viewModel.submitRating(rating)
}) {
    Text("Submit Rating")
}
```

---

## ✅ Чек-лист готовности

- [x] Firebase Firestore интеграция
- [x] Система платежей (Stripe/PayPal)
- [x] Real-time GPS отслеживание
- [x] Push Notifications (FCM)
- [x] Система рейтинга
- [x] 2ГИС маршруты
- [x] ViewModel для управления состоянием
- [ ] Actual API keys configuration (нужно добавить ваши ключи)
- [ ] Unit tests
- [ ] UI tests

---

**Версия**: 2.0.0 - Full Integration  
**Статус**: 🟢 Готово к тестированию

