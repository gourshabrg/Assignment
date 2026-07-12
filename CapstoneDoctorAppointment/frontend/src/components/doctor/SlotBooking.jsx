import { useMemo, useState } from "react";
import { formatTime } from "../../utils/format";

const PERIODS = ["Morning", "Afternoon", "Evening"];
const DATES_PER_PAGE = 6;

// Morning < 12:00, Afternoon < 16:00, Evening from 16:00.
const getPeriod = (startTime) => {
  const hour = Number(startTime.split(":")[0]);

  if (hour < 12) {
    return "Morning";
  }

  if (hour < 16) {
    return "Afternoon";
  }

  return "Evening";
};

const groupByDate = (slots) =>
  slots.reduce((grouped, slot) => {
    const key = slot.slot_date;
    grouped[key] = grouped[key] ? [...grouped[key], slot] : [slot];

    return grouped;
  }, {});

const SlotBooking = ({ slots, selectedSlotId, onSelectSlot }) => {
  const slotsByDate = useMemo(() => groupByDate(slots), [slots]);
  const dates = useMemo(() => Object.keys(slotsByDate).sort(), [slotsByDate]);

  const [selectedDate, setSelectedDate] = useState(dates[0] ?? null);
  const [period, setPeriod] = useState("Morning");
  const [pageStart, setPageStart] = useState(0);

  if (dates.length === 0) {
    return (
      <p className="text-muted-custom mb-0">
        This doctor has no available slots right now.
      </p>
    );
  }

  const visibleDates = dates.slice(pageStart, pageStart + DATES_PER_PAGE);
  const daySlots = selectedDate ? slotsByDate[selectedDate] ?? [] : [];
  const periodSlots = daySlots
    .filter((slot) => getPeriod(slot.start_time) === period)
    .sort((a, b) => a.start_time.localeCompare(b.start_time));

  const handleSelectDate = (date) => {
    setSelectedDate(date);
    onSelectSlot(null);
  };

  return (
    <div className="booking">
      <h2 className="section-heading">Select Date</h2>

      <div className="date-strip">
        <button
          type="button"
          className="strip-arrow"
          onClick={() => setPageStart((prev) => Math.max(0, prev - 1))}
          disabled={pageStart === 0}
          aria-label="Previous dates"
        >
          &#8249;
        </button>

        <div className="date-list">
          {visibleDates.map((date) => {
            const day = new Date(date);
            const isSelected = date === selectedDate;

            return (
              <button
                type="button"
                key={date}
                className={`date-card ${isSelected ? "selected" : ""}`}
                onClick={() => handleSelectDate(date)}
              >
                <span className="date-day">
                  {day.toLocaleDateString("en-IN", {
                    day: "numeric",
                    month: "short"
                  })}
                </span>
                <span className="date-weekday">
                  {day.toLocaleDateString("en-IN", { weekday: "short" })}
                </span>
                <span className="date-badge">Available</span>
              </button>
            );
          })}
        </div>

        <button
          type="button"
          className="strip-arrow"
          onClick={() =>
            setPageStart((prev) =>
              Math.min(prev + 1, Math.max(0, dates.length - DATES_PER_PAGE))
            )
          }
          disabled={pageStart + DATES_PER_PAGE >= dates.length}
          aria-label="Next dates"
        >
          &#8250;
        </button>
      </div>

      <h2 className="section-heading">Select Time</h2>

      <div className="period-tabs">
        {PERIODS.map((item) => (
          <button
            type="button"
            key={item}
            className={`period-tab ${item === period ? "selected" : ""}`}
            onClick={() => setPeriod(item)}
          >
            {item}
          </button>
        ))}
      </div>

      {periodSlots.length === 0 ? (
        <p className="text-muted-custom mb-0">
          No {period} slots available.
        </p>
      ) : (
        <div className="time-grid">
          {periodSlots.map((slot) => (
            <button
              type="button"
              key={slot.id}
              className={`time-chip ${
                slot.id === selectedSlotId ? "selected" : ""
              }`}
              onClick={() => onSelectSlot(slot.id)}
            >
              {formatTime(slot.start_time)} - {formatTime(slot.end_time)}
            </button>
          ))}
        </div>
      )}
    </div>
  );
};

export default SlotBooking;
