import Foundation

/// Schedules toast dismissal callbacks after a short delay.
protocol ToastSchedulerProtocol: AnyObject {
    func schedule(duration: TimeInterval, handler: @escaping () -> Void)
    func cancel()
}

final class ToastScheduler: ToastSchedulerProtocol {
    private var workItem: DispatchWorkItem?
    private let queue: DispatchQueue
    
    init(queue: DispatchQueue = .main) {
        self.queue = queue
    }
    
    func schedule(duration: TimeInterval, handler: @escaping () -> Void) {
        workItem?.cancel()
        let item = DispatchWorkItem(block: handler)
        workItem = item
        queue.asyncAfter(deadline: .now() + duration, execute: item)
    }
    
    func cancel() {
        workItem?.cancel()
        workItem = nil
    }
}

