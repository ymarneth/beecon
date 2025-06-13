import "./assets/css/main.css";

import { createApp, h, provide } from "vue";
import { createRouter, createWebHistory } from 'vue-router';
import ui from '@nuxt/ui/vue-plugin';
import App from "./App.vue";
import PageIndex from "@/pages/PageIndex.vue";

import {
    ApolloClient,
    InMemoryCache,
    split, createHttpLink
} from '@apollo/client/core';
import { GraphQLWsLink } from '@apollo/client/link/subscriptions';
import { createClient } from 'graphql-ws';
import { getMainDefinition } from '@apollo/client/utilities';
import { DefaultApolloClient } from '@vue/apollo-composable';

// Apollo
const httpLink = createHttpLink({
    uri: 'http://localhost:8080/graphql',
})

const wsLink = new GraphQLWsLink(createClient({
    url: 'ws://localhost:8080/graphql',
}));

const link = split(
    ({ query }) => {
        const def = getMainDefinition(query);
        return def.kind === 'OperationDefinition' && def.operation === 'subscription';
    },
    wsLink,
    httpLink
);

const cache = new InMemoryCache()

const apolloClient = new ApolloClient({
    link,
    cache,
})

// APP
const app = createApp({
    setup() {
        provide(DefaultApolloClient, apolloClient)
    },

    render: () => h(App),
})

// Router
const router = createRouter({
    routes: [
        { path: '/', component: PageIndex },
    ],
    history: createWebHistory()
})

app.use(router)
app.use(ui)

app.mount('#app')
