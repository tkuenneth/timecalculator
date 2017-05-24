using System;
using Android.App;
using Android.Views;
using Android.Widget;
using Android.OS;
using System.Text;

namespace Zeitrechner
{
    [Activity(Label = "@string/ApplicationName", MainLauncher = true, Icon = "@drawable/icon")]
    public class MainActivity : Activity
    {

        private TextView inputTextView;
        private TextView outputTextView;
        private ScrollView scrollView;

        private readonly StringBuilder sb = new StringBuilder();

        private int result;
        private String lastOp;

        protected override void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);
            SetContentView(Resource.Layout.layout);

            inputTextView = (TextView)FindViewById(Resource.Id.inputTextView);
            outputTextView = (TextView)FindViewById(Resource.Id.outputTextView);
            scrollView = (ScrollView)FindViewById(Resource.Id.scrollView);
            clearInputTextView();
            resetOutputTextView();
        }

        // See http://stackoverflow.com/a/20210562
        [Java.Interop.Export("handleButtonClick")]
        public void handleButtonClick(View v)
        {
            if (v is Button)
            {
                String txt = ((Button)v).Text.ToString();
                switch (txt)
                {
                    case "CE":
                        if (inputTextView.Length() > 0)
                        {
                            clearInputTextView();
                        }
                        else
                        {
                            resetOutputTextView();
                        }
                        break;
                    case "+":
                        handleInput(txt);
                        break;
                    case "-":
                        handleInput(txt);
                        break;
                    case ":":
                        inputTextView.Append(txt);
                        break;
                    case "=":
                        handleInput(txt);
                        break;
                    default:
                        inputTextView.Append(txt);
                        break;
                }
            }
        }

        private void handleInput(String op)
        {
            String line = inputTextView.Text.ToString();
            if (!line.Contains(":"))
            {
                if (line.Length == 4)
                {
                    line = line.Substring(0, 2) + ":" + line.Substring(2);
                }
                else
                {
                    int _total = 0;
                    if (line.Length > 0)
                    {
                        _total = Int32.Parse(line);
                    }
                    int _hours = _total / 60;
                    int _minutes = _total % 60;
                    // Use Trim() to remove leading blanks
                    line = getTimeAsString(_hours, _minutes).Trim();
                }
            }
            int pos = line.IndexOf(':');
            int hours = getIntFromString(line.Substring(0, pos));
            int minutes = getIntFromString(line.Substring(pos + 1));
            int total = (hours * 60) + minutes;
            if ("-".Equals(lastOp))
            {
                total = -total;
            }
            lastOp = op;
            result += total;
            if (sb.Length > 0)
            {
                sb.Append("\n");
            }
            sb.Append(getTimeAsString(hours, minutes))
                    .Append(" ")
                    .Append(op);
            if ("=".Equals(op))
            {
                int temp = (result < 0) ? -result : result;
                hours = temp / 60;
                minutes = temp % 60;
                String strResult = getTimeAsString(hours, minutes).Trim();
                sb.Append(" ")
                        .Append((result < 0) ? "-" : "")
                        .Append(strResult)
                        .Append("\n");
                result = 0;
                inputTextView.Text = strResult;
            }
            else
            {
                clearInputTextView();
            }
            updateOutputTextView();
        }

        private void clearInputTextView()
        {
            inputTextView.Text = "";
        }

        private void updateOutputTextView()
        {
            outputTextView.Text = sb.ToString();
            scrollView.FullScroll(FocusSearchDirection.Down);
        }

        private void resetOutputTextView()
        {
            result = 0;
            lastOp = "+";
            sb.Length = 0;
            updateOutputTextView();
        }

        private String getTimeAsString(int hours, int minutes)
        {
            return String.Format("{0,3}:{1:00}", hours, minutes);
        }

        private int getIntFromString(String s)
        {
            try
            {
                return Int32.Parse(s);
            } catch (FormatException e)
            {
                return 0;
            }
        }
    }
}

