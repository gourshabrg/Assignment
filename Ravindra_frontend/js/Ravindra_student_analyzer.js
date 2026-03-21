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

// this function take one student object and iterate  marks array of object each element and calculate the  total marks
function getTotalMarks(student) {
  let total = 0;
  for (let subject of student.marks) {
    total += subject.score;
  }
  return total;
}
// loop student array of objects and iterate each element  and print one by one its name and marks
for (let student of students) {
  console.log(`${student.name} total marks ${getTotalMarks(student)}`);
}
