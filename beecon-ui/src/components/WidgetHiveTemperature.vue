<script setup>

import {computed} from "vue";
import {useQuery} from "@vue/apollo-composable";
import gql from "graphql-tag";
import {Line} from "vue-chartjs";

const props = defineProps({
  hiveIds: {
    type: Array,
    default: () => []
  }
})

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
}

function loadTemperatureData(hiveIds = []) {
  const twoWeeksAgo = new Date();
  twoWeeksAgo.setDate(twoWeeksAgo.getDate() - 14);
  return useQuery(gql`
    query {
      hiveSensorReadings(hiveIds: [${hiveIds.map(entry => `"${entry}"`)}]) {
    	name
    	sensorReadings(interval: DAY, from: "${twoWeeksAgo.toISOString()}") {
          avg {
            createdAt
        	  temperature
          }
        }
      }
    }
  `)
}

const sensorReadingChartData = computed(() => {
  if (!result || !result.value || result.value.hiveSensorReadings.length === 0) {
    return null;
  }

  const dates = [];
  for (let i = 0; i < 14; i++) {
    const date = new Date();
    date.setDate(date.getDate() - i);
    dates.push(date);
  }

  const datasets = []
  for (let element of result.value.hiveSensorReadings) {
    let randomHexColor = Math.floor(Math.random()*16777215).toString(16);
    let randomColor = `#${randomHexColor}`;

    // make sure each dataset has an entry for each date
    const dataMap = new Map();
    for (let item of element.sensorReadings) {
      const date = new Date(item.avg.createdAt);
      const dateString = date.toISOString().split('T')[0];
      dataMap.set(dateString, item.avg.temperature);
    }
    const dataArray = [];
    for (let date of dates) {
      const dateString = date.toISOString().split('T')[0];
      dataArray.push(dataMap.get(dateString) || null);
    }

    datasets.push({
      label: element.name,
      data: dataArray.reverse(),
      borderColor: randomColor,
      pointBackgroundColor: randomColor,
      pointBorderColor: randomColor,
      tension: 0.4,
    });
  }
  return {
    labels: dates.map(date => date.toLocaleDateString('en-US', { month: 'numeric', day: 'numeric' })).reverse(),
    datasets: datasets
  }
});

const { result } = loadTemperatureData(props.hiveIds);
</script>

<template>
<div v-if="result">
  <UCard>
    <div class="grid grid-cols-1 gap-4">
      <h3 class="font-bold">Temperatures of all hives in the last two weeks</h3>
      <div class="relative w-full h-96">
        <Line v-if="sensorReadingChartData" :data="sensorReadingChartData" :options="chartOptions" />
      </div>
    </div>
  </UCard>
</div>
</template>

<style scoped>

</style>