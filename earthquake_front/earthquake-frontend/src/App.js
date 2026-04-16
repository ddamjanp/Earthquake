import { useState, useEffect } from "react";
import axios from "axios";
import { MapContainer, TileLayer, CircleMarker, Popup } from "react-leaflet";
import "leaflet/dist/leaflet.css";

const BASE_URL = "http://localhost:8080/earthquakes";

function App() {
  const [earthquakes, setEarthquakes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [minMagnitude, setMinMagnitude] = useState("");
  const [afterTime, setAfterTime] = useState("");
  const [timeError, setTimeError] = useState("");

  const loadEarthquakes = async () => {
    setLoading(true);
    try {
      const response = await axios.get(BASE_URL);
      setEarthquakes(response.data);
    } catch (error) {
      console.error("Error loading earthquakes:", error);
    } finally {
      setLoading(false);
    }
  };

 const fetchEarthquakes = async () => {
    setLoading(true);
    console.log("fetchEarthquakes called");
    try {
      console.log("calling /get");
      await axios.get(`${BASE_URL}/get`);
      console.log("calling /earthquakes");
      const response = await axios.get(BASE_URL);
      setEarthquakes(response.data);
    } catch (error) {
      console.error("Error fetching earthquakes:", error);
    } finally {
      setLoading(false);
    }
};

  const filterByMagnitude = async () => {
    if (!minMagnitude) return;
    try {
      const response = await axios.get(`${BASE_URL}/filter/magnitude?minValue=${minMagnitude}`);
      setEarthquakes(response.data);
    } catch (error) {
      console.error("Error filtering by magnitude:", error);
    }
  };

  const timeRegex = /^(0[1-9]|1[0-2]):([0-5][0-9]):([0-5][0-9]) (AM|PM)$/;

  const parseTimeToTimestamp = (timeStr) => {
    const [time, period] = timeStr.split(" ");
    const [hours, minutes, seconds] = time.split(":").map(Number);
    const date = new Date();
    let h = hours;
    if (period === "AM" && hours === 12) h = 0;
    if (period === "PM" && hours !== 12) h = hours + 12;
    date.setHours(h, minutes, seconds, 0);
    return date.getTime();
  };

  const filterByTime = async () => {
    if (!afterTime) return;
    if (!timeRegex.test(afterTime)) {
      setTimeError("Invalid format. Use hh:mm:ss AM/PM");
      return;
    }
    const timestamp = parseTimeToTimestamp(afterTime);
    try {
      const response = await axios.get(`${BASE_URL}/filter/after?time=${timestamp}`);
      setEarthquakes(response.data);
      setTimeError("");
    } catch (error) {
      console.error("Error filtering by time:", error);
    }
  };

  const deleteEarthquake = async (id) => {
    try {
      await axios.delete(`${BASE_URL}/${id}`);
      setEarthquakes(earthquakes.filter((eq) => eq.id !== id));
    } catch (error) {
      console.error("Error deleting earthquake:", error);
    }
  };

  const goHome = () => {
    setMinMagnitude("");
    loadEarthquakes();
  };

  useEffect(() => {
    loadEarthquakes();
  }, []);

  return (
    <div style={{ padding: "20px" }}>
      <div style={{ display: "flex", alignItems: "center", gap: "20px" }}>
        <button onClick={goHome}>🏠 Home</button>
        <h1 style={{ margin: 0 }}>Earthquakes!</h1>
        <button onClick={fetchEarthquakes}>Refresh Data</button>
      </div>

      <div style={{ marginTop: "20px", display: "flex", gap: "10px", alignItems: "center" }}>
        <input
          type="number"
          placeholder="Min magnitude"
          value={minMagnitude}
          onChange={(e) => setMinMagnitude(e.target.value)}
        />
        <button onClick={filterByMagnitude}>Filter by Magnitude</button>
      </div>

      <div style={{ marginTop: "10px", display: "flex", gap: "10px", alignItems: "center" }}>
        <input
          type="text"
          placeholder="hh:mm:ss AM/PM"
          value={afterTime}
          onChange={(e) => setAfterTime(e.target.value)}
        />
        <button onClick={filterByTime}>Filter by Time</button>
        {timeError && <span style={{ color: "red" }}>{timeError}</span>}
      </div>

      {loading ? (
        <p>Loading...</p>
      ) : (
        <table border="1" cellPadding="8" style={{ marginTop: "20px", width: "100%" }}>
          <thead>
            <tr>
              <th>Title</th>
              <th>Magnitude</th>
              <th>Mag Type</th>
              <th>Place</th>
              <th>Time</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {earthquakes.map((eq) => (
              <tr key={eq.id}>
                <td>{eq.title}</td>
                <td>{eq.magnitude}</td>
                <td>{eq.magType}</td>
                <td>{eq.place}</td>
                <td>{new Date(eq.time).toLocaleString()}</td>
                <td>
                  <button onClick={() => deleteEarthquake(eq.id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      <MapContainer center={[20, 0]} zoom={2} style={{ height: "400px", width: "100%", marginTop: "20px" }}>
        <TileLayer
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          attribution='&copy; OpenStreetMap contributors'
        />
        {earthquakes
          .filter(eq => eq.latitude && eq.longitude)
          .map(eq => (
            <CircleMarker
              key={eq.id}
              center={[eq.latitude, eq.longitude]}
              radius={eq.magnitude ? eq.magnitude * 3 : 3}
              color="red"
            >
              <Popup>
                <b>{eq.title}</b><br />
                Magnitude: {eq.magnitude}<br />
                Place: {eq.place}<br />
                Time: {new Date(eq.time).toLocaleString()}
              </Popup>
            </CircleMarker>
          ))}
      </MapContainer>
    </div>
  );
}

export default App;