##FEATURES

Customize the progress mark margins, text (formatted strings and colors) and drawable. 

![EXAMPLE](http://oi60.tinypic.com/2lj4ax0.jpg)

##CODE EXAMPLE

        <com.vsa.seekbarindicated.SeekBarIndicated
            android:id="@+id/seekbar_indicated"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:seekbar_marginLeft="10dp"
            app:seekbar_marginRight="10dp"
            app:seekbar_indicatorText="@string/formatted_string_resource"/>

##GRADLE

    compile 'com.vsa:seekbarindicated:1.2.3'