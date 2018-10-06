package cn.lanyj.keeper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;

public class AnnotationTest {
	
	@A
	public void test(int a) {
		
	}
	
	public static void main(String[] args) {
		AnnotationTest at = new AnnotationTest();
		try {
			Method mt = at.getClass().getMethod("test", int.class);
			System.out.println(Arrays.toString(mt.getAnnotations()));
			System.out.println(Arrays.toString(mt.getAnnotationsByType(B.class)));
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
}

@Target (ElementType.METHOD)
@Retention (RetentionPolicy.RUNTIME)
@interface A {
	
}

@Target (ElementType.PARAMETER)
@Retention (RetentionPolicy.RUNTIME)
@interface B {
	
}