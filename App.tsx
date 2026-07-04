import React, { useState } from 'react';
import {
  StyleSheet,
  Text,
  View,
  TextInput,
  TouchableOpacity,
  Alert,
  Switch,
  SafeAreaView,
  NativeModules,
} from 'react-native';

// 💡 桥接点：这里直接映射我们在 Android 原生层即将调用的总控开关方法
const { GuardianBridge } = NativeModules;

export default function App() {
  const [password, setPassword] = useState('');
  const [isUnlocked, setIsUnlocked] = useState(false);
  const [isShieldActive, setIsShieldActive] = useState(false);

  const MASTER_PASSWORD = '8888'; // 💡 演示用主密码，后期可接入 AsyncStorage 动态存储

  // 验证家长密码锁
  const handleVerifyPassword = () => {
    if (password === MASTER_PASSWORD) {
      setIsUnlocked(true);
      setPassword('');
    } else {
      Alert.alert('Security Alert', 'Incorrect Master Password. Access Denied.', [
        { text: 'Retry' },
      ]);
      setPassword('');
    }
  };

  // 操控总防护开关
  const toggleShield = (value: boolean) => {
    setIsShieldActive(value);
    if (value) {
      // 🚨 前端通电：拉起安卓底层的全天候天眼拦截服务
      if (GuardianBridge) {
        GuardianBridge.startShieldService();
      } else {
        console.log('Bridge not linked yet: Starting Core Shield Simulation.');
      }
    } else {
      // 🛑 前端断电：安全释放底层 NPU 和截屏总闸
      if (GuardianBridge) {
        GuardianBridge.stopShieldService();
      } else {
        console.log('Bridge not linked yet: Stopping Core Shield Simulation.');
      }
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      {/* 顶部硬核状态栏 */}
      <View style={styles.header}>
        <Text style={styles.title}>VISIONARY GUARDIAN</Text>
        <Text style={styles.subtitle}>智 瞳 守 护 者</Text>
      </View>

      {!isUnlocked ? (
        /* 🔒 场景 A：主密码锁界面 */
        <View style={styles.card}>
          <Text style={styles.cardTitle}>🔐 PARENTAL CORE ACCESS</Text>
          <Text style={styles.label}>Enter Master Password to adjust settings:</Text>
          <TextInput
            style={styles.input}
            secureTextEntry
            placeholder="••••••"
            placeholderTextColor="#666"
            value={password}
            onChangeText={setPassword}
            keyboardType="numeric"
          />
          <TouchableOpacity style={styles.button} onPress={handleVerifyPassword}>
            <Text style={styles.buttonText}>VERIFY IDENTITY</Text>
          </TouchableOpacity>
        </View>
      ) : (
        /* 🛡️ 场景 B：解锁后的核心控制台 */
        <View style={styles.card}>
          <Text style={styles.cardTitle}>⚙️ SHIELD CONTROL PANEL</Text>
          
          <View style={styles.switchRow}>
            <View>
              <Text style={styles.switchLabel}>AI Visual Shield</Text>
              <Text style={styles.switchDesc}>
                {isShieldActive ? '🔴 ACTIVE: Intercepting harmful frames' : '⚪ STANDBY: Protection paused'}
              </Text>
            </View>
            <Switch
              trackColor={{ false: '#333', true: '#10B981' }}
              thumbColor={isShieldActive ? '#fff' : '#f4f3f4'}
              onValueChange={toggleShield}
              value={isShieldActive}
            />
          </View>

          <View style={styles.infoBox}>
            <Text style={styles.infoText}>• Device Admin: LOCKED</Text>
            <Text style={styles.infoText}>• Local NPU Engine: READY</Text>
            <Text style={styles.infoText}>• Cloud Data Sync: ZERO-KNOWLEDGE (OFFLINE)</Text>
          </View>

          <TouchableOpacity 
            style={[styles.button, { backgroundColor: '#333', marginTop: 20 }]} 
            onPress={() => setIsUnlocked(false)}
          >
            <Text style={styles.buttonText}>LOCK CONSOLE</Text>
          </TouchableOpacity>
        </View>
      )}

      {/* 底部版权标志 */}
      <Text style={styles.footer}>🛡️ Edge AI Shielding Protocol v1.0</Text>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0A0A0A', // 深邃黑，极客风
    justifyContent: 'center',
    padding: 20,
  },
  header: {
    alignItems: 'center',
    marginBottom: 40,
  },
  title: {
    fontSize: 26,
    fontWeight: '900',
    color: '#FFF',
    letterSpacing: 2,
  },
  subtitle: {
    fontSize: 14,
    color: '#10B981', // 翡翠绿，代表防御激活
    letterSpacing: 6,
    marginTop: 5,
  },
  card: {
    backgroundColor: '#141414',
    borderRadius: 12,
    padding: 24,
    borderWidth: 1,
    borderColor: '#222',
  },
  cardTitle: {
    fontSize: 16,
    fontWeight: '700',
    color: '#FFF',
    marginBottom: 20,
    letterSpacing: 1,
  },
  label: {
    color: '#AAA',
    fontSize: 14,
    marginBottom: 10,
  },
  input: {
    backgroundColor: '#1F1F1F',
    borderRadius: 8,
    height: 50,
    color: '#FFF',
    fontSize: 20,
    textAlign: 'center',
    marginBottom: 20,
    borderWidth: 1,
    borderColor: '#333',
  },
  button: {
    backgroundColor: '#10B981',
    borderRadius: 8,
    height: 50,
    justifyContent: 'center',
    alignItems: 'center',
  },
  buttonText: {
    color: '#FFF',
    fontWeight: 'bold',
    fontSize: 14,
    letterSpacing: 1,
  },
  switchRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#222',
    marginBottom: 20,
  },
  switchLabel: {
    color: '#FFF',
    fontSize: 16,
    fontWeight: '600',
  },
  switchDesc: {
    color: '#666',
    fontSize: 12,
    marginTop: 4,
  },
  infoBox: {
    backgroundColor: '#1A1A1A',
    padding: 15,
    borderRadius: 8,
  },
  infoText: {
    color: '#888',
    fontSize: 11,
    lineHeight: 18,
    fontFamily: 'Courier',
  },
  footer: {
    textAlign: 'center',
    color: '#444',
    fontSize: 11,
    marginTop: 40,
  },
});