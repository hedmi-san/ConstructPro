package constructpro.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import constructpro.DAO.AttendanceDAO;
import constructpro.DAO.WorkerSalaryConfigDAO;
import constructpro.DAO.SalaryDAO;
import constructpro.DAO.PayrollPeriodDAO;
import constructpro.DAO.WorkerBalanceDAO;

import constructpro.DTO.Attendance;
import constructpro.DTO.WorkerSalaryConfig;
import constructpro.DTO.Salary;
import constructpro.DTO.PayrollPeriod;
import constructpro.DTO.WorkerBalance;

public class PayrollService {
    private AttendanceDAO attendanceDAO;
    private WorkerSalaryConfigDAO salaryConfigDAO;
    private SalaryDAO salaryDAO;
    private PayrollPeriodDAO payrollPeriodDAO;
    private WorkerBalanceDAO workerBalanceDAO;



    public PayrollService(Connection conn) throws SQLException {
        this.attendanceDAO = new AttendanceDAO(conn);
        this.salaryConfigDAO = new WorkerSalaryConfigDAO(conn);
        this.salaryDAO = new SalaryDAO(conn);
        this.payrollPeriodDAO = new PayrollPeriodDAO(conn);
        this.workerBalanceDAO = new WorkerBalanceDAO(conn);
    }

    // 1. Generate payroll period for the given date
    public PayrollPeriod generatePayrollPeriod(LocalDate date) throws SQLException {
        int day = date.getDayOfMonth();
        LocalDate start, end;
        PayrollPeriod.PeriodType periodType;

        if (day <= 15) {
            start = date.withDayOfMonth(1);
            end = date.withDayOfMonth(15);
            periodType = PayrollPeriod.PeriodType.FIRST_HALF;
        } else {
            start = date.withDayOfMonth(16);
            end = date.withDayOfMonth(date.lengthOfMonth());
            periodType = PayrollPeriod.PeriodType.SECOND_HALF;
        }

        PayrollPeriod period = new PayrollPeriod(start, end, periodType);
        payrollPeriodDAO.addPayrollPeriod(period); // keep your existing DAO method name
        return period;
    }

    // 2. Calculate a single worker's salary for a payroll period
    public Salary calculateWorkerSalary(int workerId, PayrollPeriod period) throws SQLException {
        // count attendances within the period
        List<Attendance> attendances = attendanceDAO.getAttendanceByWorker(workerId);
        long daysWorked = attendances.stream()
            .filter(a -> !a.getAttendanceDate().isBefore(period.getStartDate())
                      && !a.getAttendanceDate().isAfter(period.getEndDate())
                      && a.isPresent())
            .count();

        // get salary config
        WorkerSalaryConfig config = salaryConfigDAO.getActiveConfig(workerId);
        if (config == null) {
            throw new IllegalStateException("No salary configuration found for worker " + workerId);
        }

        Salary salary = new Salary(workerId,
                                   period.getId(),
                                   (int) daysWorked,
                                   config.getDailyRate(),
                                   config.getPaymentPercentage());

        salaryDAO.addSalary(salary);
        updateWorkerBalance(workerId, salary);
        return salary;
    }

    // 3. Update worker running balance
    private void updateWorkerBalance(int workerId, Salary salary) throws SQLException {
        WorkerBalance balance = workerBalanceDAO.getBalanceByWorker(workerId);
        if (balance == null) {
            balance = new WorkerBalance(workerId);
            workerBalanceDAO.insert(balance); // ensure DAO has insert() - we created this earlier
        }

        balance.setTotalEarned(balance.getTotalEarned() + salary.getTotalEarned());
        balance.setTotalPaid(balance.getTotalPaid() + salary.getAmountPaid());
        balance.setTotalRetained(balance.getTotalRetained() + salary.getRetainedAmount());

        workerBalanceDAO.updateWorkerBalance(balance);
    }

    // 4. Process payroll for list of workers
    public void processPayroll(PayrollPeriod period, List<Integer> workerIds) throws SQLException {
        for (int workerId : workerIds) {
            calculateWorkerSalary(workerId, period);
        }
        period.setProcessed(true);
        payrollPeriodDAO.updatePayrollPeriod(period);
    }
}
