import * as mqtt from 'mqtt';
import * as oauth from 'oauth4webapi'
import * as dotenv from 'dotenv';
import {allowInsecureRequests} from 'oauth4webapi';
import { setGlobalDispatcher, Agent } from 'undici';
dotenv.config();

const {
    KEYCLOAK_URL,
    CLIENT_ID,
    CLIENT_SECRET,
    MQTT_BROKER,
    MQTT_TOPIC,
    TIMEOUT
} = process.env;

const issuerUrl = new URL(KEYCLOAK_URL);

setGlobalDispatcher(new Agent({
    connect: {
        rejectUnauthorized: false // ✅ Disable cert validation (only in dev!)
    }
}));

const authorizationServer = await oauth
    .discoveryRequest(issuerUrl, { algorithm: 'oidc', [allowInsecureRequests]: true })
    .then((response) => oauth.processDiscoveryResponse(issuerUrl, response))

const client = { client_id: CLIENT_ID }
const clientAuth = oauth.ClientSecretPost(CLIENT_SECRET)

// Client Credentials Grant Request & Response
async function getAccessToken() {
    const parameters = new URLSearchParams()
    parameters.set('scope', 'rabbitmq')

    const response = await oauth.clientCredentialsGrantRequest(authorizationServer, client, clientAuth, parameters, {[allowInsecureRequests]: true})

    const result = await oauth.processClientCredentialsResponse(authorizationServer, client, response)

    return result.access_token;
}

async function publishMqttMessage(message) {
    return new Promise((resolve, reject) => {
        const client = mqtt.connect(MQTT_BROKER, {
            username: '',
            password: token,
            client_id: CLIENT_ID,
            rejectUnauthorized: false
        });

        let retry = false;

        client.on('connect', () => {
            console.log('MQTT connected');
            client.publish(MQTT_TOPIC, message, {}, () => {
                console.log('Message published', message);
                client.end();
            });
        });

        client.on('error', async (err) => {
            console.error('MQTT Error:', err.message);
            client.end();

            if (!retry && err.code === 401) {
                retry = true;
                try {
                    console.log('Token may have expired, fetching new token...');
                    await publishMqttMessage(message); // Retry
                    resolve();
                } catch (tokenErr) {
                    reject(tokenErr);
                }
            } else {
                reject(err);
            }
        });
    });
}

function generateSensorData() {
    const sensorData = {
        hiveId: 'hive-1',
        temperature: (Math.random() * 30 + 20).toFixed(2), // Random temp between 20-50
        humidity: (Math.random() * 100).toFixed(2), // Random humidity between 0-100
        co2Emission: (Math.random() * 1000 + 400).toFixed(2), // Random CO2 emission between 400-1400
        precipitation: (Math.random() * 100).toFixed(2), // Random precipitation between 0-100
        createdAt: new Date().toISOString()
    }

    return JSON.stringify(sensorData);
}

async function publishSensorData() {
    const message = generateSensorData();
    try {
        console.log('Publishing sensor data...');
        await publishMqttMessage(message);
    } catch (err) {
        console.error('Failed to publish message after retry:', err.message);
    }
}

async function init() {
    console.log('Access Token retrieved: ', token);

    setInterval(() => {publishSensorData()}, TIMEOUT);

    await publishSensorData();
}

let token = await getAccessToken();
await init();
