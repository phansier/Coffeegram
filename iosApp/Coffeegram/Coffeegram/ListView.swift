import SwiftUI
import SwiftData

struct ListView: View {
    let date: Date
    @ObservedObject var viewModel: ListViewModel

    var body: some View {
        Text(dateString(from:date))
            .font(.title2)
            .bold()
        List {
            ForEach(viewModel.drinks, id: \.self) { drink in
                HStack {

                    // Drink info
                    HStack {
                        Image(systemName: drink.icon.lowercased())
                            .foregroundColor(.brown)
                            .imageScale(.large)
                        Text(drink.name)
                            .font(.body)
                        Spacer()

                    }

                    // Decrement button
                    Button(action: {
                        viewModel.decrement(drinkId: drink.id, date: date)
                    }) {
                        Image(systemName: "minus.circle.fill")
                            .foregroundColor(.red)
                            .imageScale(.large)
                    }
//                    Text("\(drink.count)")
                    Text("\(viewModel.getDrinkCount(drinkId: drink.id, for: date))")
                        .font(.headline)
                        .monospacedDigit()
                    // Increment button
                    Button(action: {
                        viewModel.increment(drinkId: drink.id, date: date)
                    }) {
                        Image(systemName: "plus.circle.fill")
                            .foregroundColor(.green)
                            .imageScale(.large)
                    }
                }
                .padding(.vertical, 4)

            }

        }
    }
    
    private func dateString(from date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "dd MMMM"
        return formatter.string(from: date)
    }
}

//#Preview {
//    ListView()
//}
