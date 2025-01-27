import SwiftUI
import SwiftData

struct CalendarView: View {
    @ObservedObject var viewModel: CalendarViewModel
    @Environment(\.modelContext) private var modelContext
    //var modelContext: ModelContext
    @Environment(\.calendar) var calendar


    private let daysOfWeek = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"]
    private let columns = Array(repeating: GridItem(.flexible()), count: 7)

    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                // Month navigation
                HStack {
                    Button(action: { viewModel.moveMonth(by: -1) }) {
                        Image(systemName: "chevron.left")
                            .imageScale(.large)
                    }

                    Spacer()

                    Text(monthYearString(from: viewModel.selectedDate))
                        .font(.title2)
                        .bold()

                    Spacer()

                    Button(action: { viewModel.moveMonth(by: 1) }) {
                        Image(systemName: "chevron.right")
                            .imageScale(.large)
                    }
                }
                .padding(.horizontal)

                // Days of week header
                LazyVGrid(columns: columns) {
                    ForEach(daysOfWeek, id: \.self) { day in
                        Text(day)
                            .font(.caption)
                            .foregroundColor(.gray)

                    }
                }
                
                // todo calendar not updated after back navigation from list

                // Calendar grid
                LazyVGrid(columns: columns, spacing: 8) {
                    ForEach(daysInMonth(), id: \.self) { date in
                        if let date = date {
                            NavigationLink(destination: ListView(date:date, viewModel: ListViewModel(modelContext:modelContext))) {
                                DayCell(date: date, count: viewModel.getTotalCupsForDate(date))
                            }
                        } else {
                            Color.clear
                        }
                    }
                }

                Spacer()
            }
            .padding()

        }
    }

    private func monthYearString(from date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "MMMM yyyy"
        return formatter.string(from: date)
    }

    private func daysInMonth() -> [Date?] {
        let interval = DateInterval(
            start: startOfMonth(for: viewModel.selectedDate),
            end: endOfMonth(for: viewModel.selectedDate)
        )

        let firstWeekday = calendar.component(.weekday, from: interval.start)
        let offsetDays = firstWeekday - 2

        let daysInMonth = calendar.dateComponents([.day], from: interval.start, to: interval.end).day! + 1

        var dates: [Date?] = Array(repeating: nil, count: offsetDays) //todo count <0 if switching to previous month

        for day in 0..<daysInMonth {
            if let date = calendar.date(byAdding: .day, value: day, to: interval.start) {
                dates.append(date)
            }
        }

        // Pad with nil values to complete the grid
        while dates.count % 7 != 0 {
            dates.append(nil)
        }

        return dates
    }

    private func startOfMonth(for date: Date) -> Date {
        let components = calendar.dateComponents([.year, .month], from: date)
        return calendar.date(from: components)!
    }

    private func endOfMonth(for date: Date) -> Date {
        let components = DateComponents(month: 1, day: -1)
        return calendar.date(byAdding: components, to: startOfMonth(for: date))!
    }
}

struct DayCell: View {
    let date: Date
    let count: Int

    var body: some View {
        VStack {
            Text("\(Calendar.current.component(.day, from: date))")
                .font(.subheadline)

            var color = count > 0 ? Color.brown : Color.clear

            HStack(spacing: 2) {
                Image(systemName: "mug.fill")
                    .font(.caption)
                Text("\(count)")
                    .font(.caption)
            }
            .foregroundColor(color)
        }
        .frame(width: 50, height: 50)
        .background(
            RoundedRectangle(cornerRadius: 8)
                .fill(Color.white)
                .shadow(color: .gray.opacity(0.2), radius: 2, x: 0, y: 1)
        )
    }
}

//#Preview {
//    CalendarView()
//}
