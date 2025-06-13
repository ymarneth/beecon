<script setup>
import {watch, ref, toRefs} from "vue";
import {useQuery, useSubscription} from "@vue/apollo-composable";
import gql from "graphql-tag";
import {Line} from "vue-chartjs";

const props = defineProps({
  activeHive: {
    type: String,
    default: "hive-1"
  }
});

const {activeHive} = toRefs(props);

const {result} = useSubscription(gql`
  subscription sensorReadingAdded($hiveId: String!) {
    sensorReadingAdded(hiveId: $hiveId) {
      id
      hiveId
      temperature
      createdAt
    }
  }
`, {hiveId: activeHive.value});

function loadLatestTemperatureData(hiveIds = []) {
  const oneMinuteAgo = new Date(Date.now() - 60 * 1000);
  return useQuery(gql`
  query {
    hiveSensorReadings(hiveIds: [${hiveIds.map(entry => `"${entry}"`)}]) {
      name
      rawSensorReadings(from: "${oneMinuteAgo.toISOString()}", to: "${new Date().toISOString()}") {
        createdAt
        temperature
      }
    }
  }
`)
}

const currentSensorReading = ref(null);

async function fetchInitialValue() {
  const {onResult} = loadLatestTemperatureData([activeHive.value]);
  onResult(({data}) => {
    const sensorReadings = data?.hiveSensorReadings[0]?.rawSensorReadings[0];

    if (sensorReadings) {
      currentSensorReading.value = sensorReadings;
    }
  });
}

fetchInitialValue();

watch(result, (data) => {
  if (data?.sensorReadingAdded) {
    currentSensorReading.value = data.sensorReadingAdded;
  }
});
</script>

<template>
  <UCard v-if="currentSensorReading">
    <div class="flex justify-between">
      <div>
        <h3 class="text-neutral-500">Current Reading</h3>
        <p class="text-lg font-bold">{{ currentSensorReading.temperature.toFixed(2) }} °C</p>
      </div>
      <div class="flex flex-col items-end justify-between">
        <div><UBadge>{{ activeHive }}</UBadge></div>
        <p class="text-sm text-neutral-500">{{ new Date(currentSensorReading.createdAt).toLocaleString() }}</p>
      </div>
    </div>
  </UCard>
</template>