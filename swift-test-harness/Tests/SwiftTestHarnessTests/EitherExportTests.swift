import Testing
import Either

@Suite struct EitherExportTests {
    @Test func testSwiftModuleLoads() {
        #expect(Bool(true), "Either swift module imported cleanly")
    }

    @Test func testEitherFromSwift() {
        let left = Either.Left(value: "hello")
        #expect(left.value != nil)
        #expect(left.toString().contains("Left"))

        let right = Either.Right(value: Int32(42))
        #expect(right.value != nil)
        #expect(right.toString().contains("Right"))
    }
}
