import { useCallback } from "react";

export default function useOperatorActions({ movingBike, setMovingBike, setStations, logMessage }) {
  const handleMoveStart = useCallback(
    (bike) => {
      if (movingBike) {
        logMessage("Already moving a bike!");
        return;
      }
      setMovingBike(bike);
      logMessage(`Started moving bike ${bike.id}`);
    },
    [movingBike, setMovingBike, logMessage]
  );

  const handleMoveComplete = useCallback(
    (stationId) => {
      if (!movingBike) {
        logMessage("No bike is being moved!");
        return;
      }
      setStations((prevStations) =>
        prevStations.map((station) => {
          if (station.bikes.includes(movingBike.id)) {
            return {
              ...station,
              bikes: station.bikes.filter((id) => id !== movingBike.id),
            };
          }
          if (station.id === stationId) {
            return {
              ...station,
              bikes: [...station.bikes, movingBike.id],
            };
          }
          return station;
        })
      );
      logMessage(`Moved bike ${movingBike.id} to station ${stationId}`);
      setMovingBike(null);
    },
    [movingBike, setStations, setMovingBike, logMessage]
  );

  return { handleMoveStart, handleMoveComplete };
}
