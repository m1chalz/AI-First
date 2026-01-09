import Foundation
@testable import PetSpot

final class ToastSchedulerFake: ToastSchedulerProtocol {
    private(set) var scheduledDurations: [TimeInterval] = []
    private var handler: (() -> Void)?
    
    func schedule(duration: TimeInterval, handler: @escaping () -> Void) {
        scheduledDurations.append(duration)
        self.handler = handler
    }
    
    func cancel() {
        handler = nil
    }
    
    func fire() {
        handler?()
        handler = nil
    }
}

