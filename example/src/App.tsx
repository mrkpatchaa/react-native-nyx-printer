import * as React from 'react';

import { StyleSheet, View, Text, Button } from 'react-native';
import { multiply, printText, NyxTextFormat } from 'react-native-nyx-printer';

export default function App() {
  const [result, setResult] = React.useState<number | undefined>();

  React.useEffect(() => {
    multiply(3, 7).then(setResult);
  }, []);

  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
      <Button
        onPress={() => {
          console.log('hey');
          printText('blah')
            .then((value) => console.log(value))
            .catch((e) => console.log(e));
          // console.log(result);
        }}
        title="Print text"
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
