export default {
    connectToSSE(onMessage) {
        const source = new EventSource('http://localhost:8080/notifications/stream')

        source.onmessage = (event) => {
            const data = JSON.parse(event.data)
            onMessage(data)
        }

        source.onerror = (err) => {
            console.error('SSE error:', err)
            source.close()
        }

        return source
    },
}
