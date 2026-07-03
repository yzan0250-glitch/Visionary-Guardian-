# Visionary Guardian (智瞳守护者) 🛡️

> **The Digital Seatbelt for Your Child's Mind.**
> An open-source, ultra-fast parental control filter that shields children from harmful visuals in real-time, without spying on their lives.

---

## 👪 To Every Anxious Parent (写给每一位焦虑的家长)

Are you worried about what your children are seeing on their phone screens late at night? 

Today's internet is flooded with graphic violence, inappropriate content, and toxic social media feeds. Traditional parental control tools try to block websites, but they fail completely inside modern dynamic apps like TikTok or Instagram. Worse, many apps resort to "spyware" tactics—reading your child's private chats or logging their messages—which completely destroys family trust.

**Visionary Guardian is built differently. It is not a spy tool. It is a protective shield.**

### ✨ What It Does for Your Family:
1. **Instant Visual Shield (实时眼部护盾):** It works in the background across the entire phone. The exact millisecond any violent, explicit, or harmful image appears on the screen (whether on a website, in a video, or inside a social app), the software instantly blurs or blacks out the display before your child's eyes can process the harm.
2. **Absolute Privacy (尊重孩子的隐私):** It does **NOT** read your child's texts. It does **NOT** listen to their conversations. It does **NOT** track who they are talking to. It only acts as an automated filter for visual safety.
3. **100% On-Device & Safe (绝不上传云端):** Everything is calculated strictly inside the phone's physical hardware. It **never** saves screenshots, **never** stores history logs, and **never** uploads any data to the internet. Your child's screen remains 100% private.
4. **Un-circumventable Control (防孩子破解):** Once activated with your master password, children cannot force-quit or uninstall the application. It runs quietly and persistently to keep them safe.

---

## 🛠️ For Developers & Tech Geeks (技术架构与实现原理)

Welcome, engineers! Visionary Guardian operates as a high-performance native pipeline wrapped in a React Native / Native Hybrid bridge. We enforce a zero-trust, ironclad local runtime to comply with top-tier global child privacy frameworks (GDPR/COPPA).

### 🏗️ System Architecture


1. **The Interceptor:** Uses Android's native `MediaProjection` API via a persistent Foreground Service to capture screen frames at optimized dynamic intervals (targeting <5% total system power consumption).
2. **The Evaluator:** Downsamples and feeds the frame into an ultra-lightweight, quantized INT8 visual classifier running on Google MediaPipe / TensorFlow Lite. It checks for raw visual features (e.g., explicit violence, weapons, adult material) without understanding or recording the actual text.
3. **The Shield:** If a threshold is breached, a high-priority system-level `WindowManager` Overlay instantly triggers a blur or protective cover before the child's eyes process the harm.

### 🔒 Privacy & Anti-Stalkerware Guarantees
* **100% Edge AI:** All visual inferences are handled locally on the device's hardware NPU/GPU. Zero network overhead for vision processing.
* **Ephemeral Memory Routing:** Screen frames are intercepted into volatile memory (RAM), processed within milliseconds, and immediately incinerated. No permanent storage, zero screenshots saved.
* **Visible Protection Policy:** This app enforces a persistent, un-hidable system notification while active. It cannot be used as spyware or background stalkerware.

---

## 🗺️ Roadmap & Current Status

We are currently in **Phase 1: Foundation Building**. The project is open for early architecture review and core native scaffolding.

- [ ] **Phase 1: Native Ironclad Foundation (Current)**
  - Implement Device Administrator protocols to prevent unauthorized app tampering or uninstallation.
  - Setup high-resiliency Boot Receivers and persistent Foreground Services for MediaProjection.
- [ ] **Phase 2: Local Brain Integration**
  - Integrate on-device TFLite/MediaPipe inference loops optimized for mobile NPUs.
  - Fine-tune lightweight binary visual classifiers for sensitive content detection.
- [ ] **Phase 3: The Guardian UI & Customization**
  - Build the React Native configuration dashboard for parents (protected by master password).
  - Add customizable dynamic blur levels and safety warnings.

---

## 🤝 Contributing

We welcome developers, UI/UX designers, and privacy advocates worldwide to join us in building a safer digital environment for the next generation. Feel free to open an Issue or submit a Pull Request.

---

## ⚖️ License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

*Disclaimer: Visionary Guardian is a protective utility intended for parents to exercise legal guardianship. It must be installed with explicit awareness of the device user via persistent notifications.*
