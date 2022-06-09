
abstract class FixRecord(length: Int,
                         s: String,
                         r: FixRecord,
                         overflowError: Boolean, underflowError: Boolean)
  extends FixEngine(length, s, r, overflowError, underflowError) with FixBasic {
}
