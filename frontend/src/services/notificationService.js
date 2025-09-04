import { EventSourcePolyfill } from "event-source-polyfill";

export default {
  connectToSSE(onMessage) {
    const token = localStorage.getItem("token"); // hoặc từ pinia store
    if (!token) {
      console.error("❌ No token found, SSE cannot connect");
      return null;
    }

    const source = new EventSourcePolyfill("http://localhost:8080/notifications/stream", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      heartbeatTimeout: 60000, // giữ connection sống lâu hơn (60s)
    });

    source.onopen = () => {
      console.log("✅ SSE connected");
    };

    source.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        onMessage(data);
      } catch (err) {
        console.error("❌ Error parsing SSE message:", err);
      }
    };

    source.onerror = (err) => {
      console.error("❌ SSE error:", err);
      // đừng close ngay, EventSourcePolyfill sẽ tự retry
    };

    return source;
  },
};
