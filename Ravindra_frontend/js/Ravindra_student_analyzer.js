// student data
const students = [
  {
    name: "Lalit",
    marks: [
      { subject: "Math", score: 78 },
      { subject: "English", score: 82 },
      { subject: "Science", score: 74 },
      { subject: "History", score: 69 },
      { subject: "Computer", score: 88 },
    ],
    attendance: 82,
  },
  {
    name: "Rahul",
    marks: [
      { subject: "Math", score: 90 },
      { subject: "English", score: 85 },
      { subject: "Science", score: 80 },
      { subject: "History", score: 76 },
      { subject: "Computer", score: 92 },
    ],
    attendance: 91,
  },
];
//1. TOTAL MARKS
// this function take one student object and iterate  marks array of object each element and calculate the  total marks
function getTotalMarks(student) {
  let total = 0;
  for (let subject of student.marks) {
    total += subject.score;
  }
  return total;
}
// loop student array of objects and iterate each element  and print one by one its name and marks
// call the getTotalMarks and print output
for (let student of students) {
  console.log(`${student.name} total marks ${getTotalMarks(student)}`);
}

//2. AVERAGE MARKS
// some as above iterate  marks array of object
// here we can use the pre function fro gating the total marks and then divide by to total length of student marks array
function getAverageMarks(student) {
  let total = getTotalMarks(student);
  return total / student.marks.length;
}

// student array of objects and iterate each element
//  call the getAverageMarks or print output
students.forEach((student) => {
  console.log(`${student.name} Average: ${getAverageMarks(student)}`);
});

// 3. SUBJECT-WISE HIGHEST

// take input array of object
function getSubjectHighest(students) {
  let result = {}; // declare the empty object for hashing

  students.forEach((student) => {
    //  iterate each student of array object
    student.marks.forEach((mark) => {
      // iterate each student marks of array object
      let subject = mark.subject; // use as key of hash
      let score = mark.score;
      // check if the key present or not
      // if present then check the pre value of current value max cur value is max update it
      // if not resent then add the value
      if (!result[subject] || score > result[subject].score) {
        result[subject] = {
          // this key value of hash
          name: student.name,
          score: score,
        };
      }
    });
  });

  return result;
}

let highest = getSubjectHighest(students);

// iterate the result array of object and print the value
for (let subject in highest) {
  console.log(
    `Highest in ${subject}: ${highest[subject].name} (${highest[subject].score})`,
  );
}

// 4. SUBJECT-WISE AVERAGE
// some as previous code only calculation logic change
function getSubjectAverage(students) {
  let data = {};
  students.forEach((student) => {
    student.marks.forEach((mark) => {
      let subject = mark.subject;
      if (!data[subject]) {
        // make the subject as key
        data[subject] = { total: 0, count: 0 }; // default value
      }
      data[subject].total += mark.score; // value of each subject total
      data[subject].count++; // how many some subject value
    });
  });
  return data;
}

let avgData = getSubjectAverage(students);
// iterate the each subject and apply avg logic
for (let subject in avgData) {
  let avg = avgData[subject].total / avgData[subject].count;
  console.log(`Average ${subject} Score: ${avg.toFixed(2)}`);
}

// 5. Determine Overall Class Topper

function getTopper(students) {
  let maxMarks = 0;
  let topper = "";

  students.forEach((student) => {
    let total = getTotalMarks(student); // function call of getTotalMarks
    if (total > maxMarks) {
      //  compare the total to getTotal if greater then update
      maxMarks = total;
      topper = student.name;
    }
  });
  return { topper, maxMarks };
}
let top = getTopper(students);
console.log(`Class Topper: ${top.topper} with ${top.maxMarks} marks`);

// 6. GRADE LOGIC

function getGrade(student) {
  let avg = getAverageMarks(student);
  // fail if any subject ≤ 40
  for (let i = 0; i < student.marks.length; i++) {
    if (student.marks[i].score <= 40) {
      return `Fail (Failed in ${student.marks[i].subject})`;
    }
  }
  // fail if attendance < 75
  if (student.attendance < 75) {
    return "Fail (Low Attendance)";
  }
  // grade
  if (avg >= 85) return "A";
  else if (avg >= 70) return "B";
  else if (avg >= 50) return "C";
  else return "fail";
}

students.forEach((student) => {
  console.log(`${student.name} Grade: ${getGrade(student)}`);
});
