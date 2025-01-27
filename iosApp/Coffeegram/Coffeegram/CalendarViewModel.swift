import Foundation
import SwiftData

class CalendarViewModel: ObservableObject {
    @Published var selectedDate = Date()
    private var modelContext: ModelContext

    init(modelContext: ModelContext) {
        self.modelContext = modelContext
    }

    func moveMonth(by monthOffset: Int) {
        if let newDate = Calendar.current.date(byAdding: .month, value: monthOffset, to: selectedDate) {
            selectedDate = newDate
        }
    }

    func getTotalCupsForDate(_ date: Date) -> Int {
//        let calendar = Calendar.current
//        let predicate = #Predicate<DailyConsumption> { consumption in
//            calendar.isDate(consumption.date, inSameDayAs: date)
//        }
//        let descriptor = FetchDescriptor<DailyConsumption>(predicate:predicate)
        let calendar = Calendar.current
//        let targetDateComponents = calendar.dateComponents([.year, .month, .day], from: date)
//
//        let predicate = #Predicate<DailyConsumption> { consumption in
//            let consumptionDateComponents = calendar.dateComponents([.year, .month, .day], from: consumption.date)
//            return consumptionDateComponents.year == targetDateComponents.year &&
//                   consumptionDateComponents.month == targetDateComponents.month &&
//                   consumptionDateComponents.day == targetDateComponents.day
//        }
        let startOfDay = calendar.startOfDay(for: date)
        let endOfDay = calendar.date(byAdding: .day, value: 1, to: startOfDay)!

        let predicate = #Predicate<DailyConsumption> { consumption in
            consumption.date >= startOfDay && consumption.date < endOfDay
        }
        let descriptor = FetchDescriptor<DailyConsumption>(predicate:predicate)
//        let descriptor = FetchDescriptor<DailyConsumption>(predicate:CalendarViewModel.predicater(date, calendar))

        guard let consumptions = try? modelContext.fetch(descriptor) else { return 0 }
        return consumptions.reduce(0) { $0 + $1.count }
    }
}
