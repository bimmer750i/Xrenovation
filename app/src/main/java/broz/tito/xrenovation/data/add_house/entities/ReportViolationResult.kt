package broz.tito.xrenovation.data.add_house.entities

open class ReportViolationResult

class PendingReportViolationResult : ReportViolationResult()

class SuccessReportViolationResult(val name : String) : ReportViolationResult()

class FailureReportViolationResult(val errorMessage : String) : ReportViolationResult()