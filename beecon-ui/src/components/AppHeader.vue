<script setup>
import {computed} from "vue";

const props = defineProps({
    name: String
})

const greeting = computed(() => {
  const name = props.name;

  const greetings = [
    `Buzzing by to say hi, ${name}! 🐝`,
    `Hope your day is sweeter than honey, ${name}! 🍯🌼`,
    `Bee-lieve you're amazing, ${name}! ✨🐝`,
    `Have a bee-autiful day, ${name}! 🌸🐝`,
    `Just buzzing in with good vibes for you, ${name}! 💛🐝`,
    `You're the bee's knees, ${name}! 😎🐝`,
    `Stay buzzy and bright, ${name}! 🌞🐝`,
    `Wishing you a hive full of happiness, ${name}! 🎉🍯`,
    `Bee kind, bee happy, bee you, ${name}! 🌻🐝`,
    `Sending you a little buzz of joy, ${name}! 💌🐝`,

    `${name}, buzzing by to say hi! 🐝`,
    `${name}, hope your day is sweeter than honey! 🍯🌼`,
    `${name}, bee-lieve you're amazing! ✨`,
    `${name}, have a bee-autiful day! 🌸`,
    `${name}, just buzzing in with good vibes! 💛`,
    `${name}, you're the bee's knees! 😎🐝`,
    `${name}, stay buzzy and bright! 🌞`,
    `${name}, wishing you a hive full of happiness! 🎉`,
    `${name}, bee kind, bee happy, bee you! 🌻`,
    `${name}, sending you a little buzz of joy! 💌`,

    `Hey ${name}, just buzzing by your way to sprinkle some sweet honey vibes on your day! 🐝🍯`,
    `${name}, you're the queen (or king!) of the hive — stay golden and keep shining! 👑✨`,
    `Buzz buzz, ${name}! Hope your day is filled with sunshine, flowers, and lots of sweet moments! 🌞🌸🐝`,
    `${name}, you make the world a little sweeter — bee-lieve it! 🍯💛`,
    `Wings up, ${name}! It's time to soar through the day with all the buzz and brilliance you bring! 🐝🚀`,
    `Hive-five, ${name}! You're doing bee-yond amazing! 🙌🐝`,
    `${name}, you’re the honey in everyone's tea — never forget how sweet you are! 🍯☕`,
    `Bee-cause you're awesome, ${name}, here's a big buzz of love and positivity! 💖🐝`,
    `${name}, may your day bee as bright as a field full of wildflowers! 🌼🌸🌻`,
    `A little buzz told me you're incredible, ${name} — keep spreading that sweetness! ✨🐝`,
  ];

  return greetings[Math.floor(Math.random() * greetings.length)];
})
</script>

<template>


  <div class="flex gap-4 my-8 items-center">
    <img src="/Beecon_logo.png" alt="Beecon Logo" class="w-48 h-48" />
    <div>
      <h1 class="font-bold text-primary text-3xl">Beecon</h1>
      <p>
        Track your hive and keep your bees happy! <br/>
        Beecon is a web application that allows you to monitor the temperature of your beehive.
      </p>
    </div>
  </div>

  <h2 class="text-xl font-bold" >{{greeting}}</h2>
</template>

<style scoped>

</style>