# Taller 6 — Caso de estudio: Sistema de notificaciones

Sistema de notificaciones de escritorio que combina los patrones **Bridge** y **Adapter**, con interfaz gráfica en modo oscuro y tarjetas estilo glassmorphism.

---

## Caso de estudio

### Contexto

Se necesita un sistema que permita enviar notificaciones por distintos **canales** (email, SMS, push) y con distintos **tipos** (urgente, informacional). Además, el canal de push depende de un servicio externo (p. ej. Firebase) con una API distinta a la del resto de canales.

### Objetivos

- Poder añadir nuevos tipos de notificación o nuevos canales sin multiplicar clases.
- Integrar el servicio externo de push sin cambiar la lógica del sistema.
- Ofrecer una interfaz clara para enviar notificaciones y ver el historial.

### Solución: Bridge + Adapter

| Patrón   | Rol en el sistema |
|----------|--------------------|
| **Bridge**  | Separa la **abstracción** (tipo de notificación: Urgent, Informational) de la **implementación** (canal: Email, SMS, Push). Así se pueden combinar cualquier tipo con cualquier canal sin una clase por cada par. |
| **Adapter** | El proveedor externo de push no implementa nuestra interfaz `NotificationChannel`. `PushChannelAdapter` adapta su API (`pushToDevice(token, title, body)`) a `deliver(recipient, content)`, de modo que Push se usa como un canal más del Bridge. |

---

## Patrón Bridge

- **Abstracción:** `Notification` (abstracta) y sus variantes `UrgentNotification`, `InformationalNotification`. Definen *qué* se envía (formato del mensaje).
- **Implementación:** interfaz `NotificationChannel` con método `deliver(recipient, content)`. Define *cómo* se envía.
- Cada notificación recibe un canal por composición; el mismo tipo puede usar Email, SMS o Push sin nuevas subclases.

```
Notification (abstracción)          NotificationChannel (implementación)
├── UrgentNotification     ──────►  EmailChannel
├── InformationalNotification       SmsChannel
                                 ► PushChannelAdapter (Adapter)
```

---

## Patrón Adapter

- **Objetivo:** Integrar `ExternalPushService` (API: `pushToDevice(deviceToken, title, body)`) sin cambiar el resto del código.
- **Solución:** `PushChannelAdapter` implementa `NotificationChannel` y traduce `deliver(recipient, content)` a la llamada del servicio externo.
- El sistema trata Push como un canal más; solo el adapter conoce la API externa.

---

## Estructura del proyecto

| Archivo | Descripción |
|---------|-------------|
| `Notification.java` | Abstracción Bridge: notificación con canal inyectado. |
| `UrgentNotification.java` | Tipo de notificación (ej. prefijo [URGENT]). |
| `InformationalNotification.java` | Tipo de notificación (ej. prefijo [INFO]). |
| `NotificationChannel.java` | Interfaz de implementación del Bridge (canal de envío). |
| `EmailChannel.java` | Canal email. |
| `SmsChannel.java` | Canal SMS. |
| `ExternalPushService.java` | Simula el servicio externo de push (API distinta). |
| `PushChannelAdapter.java` | Adapter: adapta ExternalPushService a NotificationChannel. |
| `NotificationTheme.java` | Tema oscuro y colores de la UI. |
| `NotificationCardPanel.java` | Tarjeta flotante con estado, tiempo y botones. |
| `NotificationUI.java` | Ventana principal: formulario y feed de tarjetas. |
| `NotificationDemoMain.java` | Demo por consola (sin interfaz gráfica). |

---

## Requisitos

- JDK 8 o superior.

---

## Compilar y ejecutar

Desde la raíz del proyecto:

```bash
# Compilar
javac -encoding UTF-8 -d out src/taller6/*.java

# Ejecutar la interfaz gráfica
java -cp out taller6.NotificationUI
```

Para ejecutar solo la demo por consola:

```bash
java -cp out taller6.NotificationDemoMain
```

---

## Interfaz

- **Modo oscuro** (#1E1E1E) con acentos de color por estado (verde/rojo/azul).
- **Formulario:** tipo (Urgent / Informational), canal (Email / SMS / Push), destinatario y mensaje.
- **Tarjetas:** cada notificación enviada se muestra como una tarjeta con icono por estado, título, descripción, “hace X min” y botones *Cerrar* / *Ver detalles*.
