<script setup>
import {ref} from "vue";
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  LineElement,
  CategoryScale,
  LinearScale,
  PointElement
} from 'chart.js'
import WidgetHiveTemperature from "@/components/WidgetHiveTemperature.vue";
import HiveOverviewWidget from "@/components/HiveOverviewWidget.vue";
import LatestSensorValueWidget from "@/components/LatestSensorValueWidget.vue";
import AppHeader from "@/components/AppHeader.vue";

ChartJS.register(Title, Tooltip, Legend, LineElement, CategoryScale, LinearScale, PointElement)

const me = ref(null);
const hives = ref(null);

const fetchUserInfo = async () => {
  try {
    const response = await fetch("/api/me");
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

    me.value = await response.json();
  } catch (error) {
    console.error("Error fetching data:", error);
  }
};

const fetchHivesData = async () => {
  try {
    const response = await fetch("/api/hives");

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const hiveList = await response.json();
    hives.value = hiveList.map(hive => ({
      value: hive.id,
      label: hive.name,
    }));
  } catch (error) {
    console.error("Error fetching data:", error);
  }
};

fetchUserInfo();
fetchHivesData();
</script>

<template>
  <UContainer class="mt-8">
    <AppHeader v-if="me" :name="me.name"/>

    <UAlert
        v-if="hives && hives.length === 0"
        color="neutral"
        title="Hives not found"
        description="No hives are currently associated with your account. Therefore we have nothing to display."
    />

    <div v-if="hives && hives.length > 0">
      <div class="mt-6 grid gap-5 grid-cols-3">
        <div v-for="hive in hives" :key="hive.value">
          <LatestSensorValueWidget
              :activeHive="hive.value"
          ></LatestSensorValueWidget>
        </div>
      </div>

      <HiveOverviewWidget
          class="mt-5"
          :hives="hives"
      ></HiveOverviewWidget>

      <WidgetHiveTemperature
          class="mt-5"
          :hive-ids="hives.map(hive => hive.value)"
      ></WidgetHiveTemperature>
    </div>

    <a
        href="/api/logout"
        class="block text-gray-500 underline my-8"
    >
      Logout
    </a>
  </UContainer>
</template>

<style scoped>

</style>