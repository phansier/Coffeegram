import Foundation

class CalendarViewModel: ObservableObject {
    @Published var selectedDate = Date()
    @Published var dailyCounts: [DailyCoffeeCount] = []

    // Sample data - replace with actual data storage
    init() {
        // Generate some sample data for the current month
        let calendar = Calendar.current
        let currentMonth = calendar.component(.month, from: Date())
        let year = calendar.component(.year, from: Date())

        for day in 1...31 {
            if let date = calendar.date(from: DateComponents(year: year, month: currentMonth, day: day)) {
                dailyCounts.append(DailyCoffeeCount(date: date, totalCups: Int.random(in: 0...5)))
            }
        }
    }

    func moveMonth(by monthOffset: Int) {
        if let newDate = Calendar.current.date(byAdding: .month, value: monthOffset, to: selectedDate) {
            selectedDate = newDate
            // Here you would typically fetch data for the new month
        }
    }

    func getCupsForDate(_ date: Date) -> Int {
        return dailyCounts.first(where: { Calendar.current.isDate($0.date, inSameDayAs: date) })?.totalCups ?? 0
    }
}
