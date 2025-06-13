<script setup>
import {ref, watch, computed, onMounted} from "vue";
import { Line } from 'vue-chartjs'

const props = defineProps({
  hives: {
    type: Array,
    default: () => []
  }
});

const activeTime = ref('day')
const activeHive = ref(props.hives[0]?.value || null);
const sensorReadings = ref(null);

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
}

const earliestDayInChart = computed(() => {
  const currentDate = new Date();
  currentDate.setHours(0, 0, 0, 0);
  const newDate = new Date(currentDate);

  switch (activeTime.value) {
    case 'year':
      newDate.setFullYear(currentDate.getFullYear() - 1);
      break;
    case 'month':
      newDate.setMonth(currentDate.getMonth() - 1);
      break;
    case 'week':
      newDate.setDate(currentDate.getDate() - 7);
      break;
    case 'day':
      newDate.setDate(currentDate.getDate() - 1);
      break;
    default:
      break;
  }

  return newDate.toISOString();
});

const fetchSensorReadingsData = async () => {
  sensorReadings.value = null;

  const hives = props.hives.map(hive => hive.value);

  if (!hives || hives.length === 0) {
    console.error("No hives available to fetch readings.");
    return;
  }

  const hiveId = activeHive.value;

  if (!hiveId) {
    console.error("No hive ID found for the selected hive.");
    return;
  }

  try {
    const earliestDayInChartTemp = earliestDayInChart.value;

    const response = await fetch(
        `/api/hives/${hiveId}/readings?after=${earliestDayInChartTemp}`,
    );

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    sensorReadings.value = await response.json();
  } catch (error) {
    console.error("Error fetching data:", error);
  }
};

onMounted(() => {
  fetchSensorReadingsData();
});

const sensorReadingChartData = computed(() => {
  if (!sensorReadings.value || sensorReadings.length === 0) {
    return null;
  }

  let labels = [];

  switch (activeTime.value) {
    case 'year':
      labels = sensorReadings.value.map(item => new Date(item.createdAt).toLocaleDateString('en-US', { month: 'short', day: 'numeric' }));
      break;
    case 'month':
      labels = sensorReadings.value.map(item => new Date(item.createdAt).toLocaleDateString('en-US', { month: 'short', day: 'numeric' }));
      break;
    case 'week':
      labels = sensorReadings.value.map(item => new Date(item.createdAt).toLocaleDateString('en-US', { weekday: 'short' }));
      break;
    case 'day':
      labels = sensorReadings.value.map(item => new Date(item.createdAt).toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' }));
      break;
    default:
      break;
  }

  return {
    labels: labels,
    datasets: [{
      label: 'Temperature (°C)',
      data: sensorReadings.value.map(item => item.temperature),
      borderColor: '#51a2ff',
      pointBackgroundColor: '#51a2ff',
      pointBorderColor: '#51a2ff',
      tension: 0.4,
    }]
  }
});
</script>

<template>
  <UCard class="mt-6">
    <div class="grid grid-cols-1 gap-4">
      <UTabs
          v-model="activeHive"
          @update:modelValue="fetchSensorReadingsData"
          :content="false"
          :items="hives"
          class="w-full"
      />
      <UTabs
          v-model="activeTime"
          @update:modelValue="fetchSensorReadingsData"
          color="neutral"
          :content="false"
          :items="[
          {
            label: 'Last Year',
            value: 'year'
          },
          {
            label: 'Last Month',
            value: 'month'
          },
          {
            label: 'Last Week',
            value: 'week'
          },
          {
            label: '24 h',
            value: 'day'
          }
        ]"
          class="w-full"
      />
      <div class="relative w-full h-96">
        <Line v-if="sensorReadingChartData" :data="sensorReadingChartData" :options="chartOptions" />
      </div>
    </div>
  </UCard>
</template>

<style scoped>

</style>