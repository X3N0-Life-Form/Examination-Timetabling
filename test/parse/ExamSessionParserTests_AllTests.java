package parse;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ExamSessionParserTests_regular.class,
		ExamSessionPerserTests_flexibility.class })
public class ExamSessionParserTests_AllTests {

}
